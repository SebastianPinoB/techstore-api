package api.controller;

import api.dto.ProductoDTO;
import api.model.Producto;
import api.repository.ProductoRepository;
import api.service.ProcesarProductoFunction;
import api.service.ProductoService;
import api.service.ServerlessService;
import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final SqsClient sqsClient;
    private final String queueUrl;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProcesarProductoFunction procesarProductoFunction;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ServerlessService serverlessService;

    // Inyectamos SqsClient y la URL de la cola TODO junto en el constructor
    // principal
    @Autowired
    public ProductoController(SqsClient sqsClient, @Value("${aws.sqs.queue.url}") String queueUrl) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
    }

    // Inyecta la URL de la cola desde variables de entorno en la Task Definition de
    // ECS
    @PostConstruct
    public void init() {
        System.out.println("====== QUEUE URL CORRECIENDO INYECCIÓN = " + queueUrl);
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
            System.out.println("Entrando a enviarAuditoria");

            // Obtiene el username/email almacenado por el filtro JWT
            // Validamos si hay un usuario autenticado para evitar NullPointerException
            String usuario = "Anónimo";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                usuario = auth.getName();
            }

            String fecha = Instant.now().toString();

            // Formatear el JSON idéntico al solicitado por la rúbrica
            String jsonPayload = String.format(
                    "{\"accion\": \"%s\", \"productoId\": %d, \"usuario\": \"%s\", \"fecha\": \"%s\"}",
                    accion, productoId, usuario, fecha);

            System.out.println("Payload generado: " + jsonPayload);
            System.out.println("Enviando a URL: " + queueUrl);

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(jsonPayload)
                    .build();

            sqsClient.sendMessage(sendMsgRequest);
            System.out.println("¡Mensaje enviado exitosamente a SQS!");
        } catch (Exception e) {
            // Logear el error para evitar que la caída de SQS rompa la respuesta del
            // cliente
            System.err.println("Error enviando auditoría a SQS: " + e.getMessage());
            e.printStackTrace();
        }
    }
}