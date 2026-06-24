package api.controller;

import api.dto.ProductoDTO;
import api.model.Producto;
import api.repository.ProductoRepository;
import api.service.ProcesarProductoFunction;
import api.service.ProductoService;
import api.service.ServerlessService;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final SqsClient sqsClient;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProcesarProductoFunction procesarProductoFunction;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ServerlessService serverlessService;

    // Inyecta la URL de la cola desde variables de entorno en la Task Definition de
    // ECS
    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    ProductoController(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        // Guardado inmediato en base de datos local
        Producto nuevoProducto = productoRepository.save(producto);

        // Envia mensaje de auditoria asincrono a SQS
        enviarAuditoria("CREAR", nuevoProducto.getId());

        // Responder de inmediato al cliente (Postman)
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> modificar(@PathVariable Long id,
            @RequestBody ProductoDTO dto) {
        Producto productoModificado = productoService.modificar(id, dto);

        // Envia mensaje de auditoria tras modificar
        enviarAuditoria("MODIFICAR", id);

        return ResponseEntity.ok(productoModificado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        
        // Envir mensaje de auditoria tras eliminar
        enviarAuditoria("ELIMINAR", id);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Metodo auxiliar para centralizar la construcción del JSON y el envio a Amazon
     * SQS
     */
    private void enviarAuditoria(String accion, Long productoId) {
        try {
            // Obtiene el username/email almacenado por el filtro JWT
            String usuario = SecurityContextHolder.getContext().getAuthentication().getName();
            String fecha = Instant.now().toString();

            // Formatear el JSON idéntico al solicitado por la rúbrica
            String jsonPayload = String.format(
                    "{\"accion\": \"%s\", \"productoId\": %d, \"usuario\": \"%s\", \"fecha\": \"%s\"}",
                    accion, productoId, usuario, fecha);

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(jsonPayload)
                    .build();

            sqsClient.sendMessage(sendMsgRequest);
        } catch (Exception e) {
            // Logear el error para evitar que la caída de SQS rompa la respuesta del
            // cliente
            System.err.println("Error enviando auditoría a SQS: " + e.getMessage());
        }
    }
}