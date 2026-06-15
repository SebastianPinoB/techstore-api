package api.controller;

import api.dto.ProductoDTO;
import api.model.Producto;
import api.repository.ProductoRepository;
import api.service.ProcesarProductoFunction;
import api.service.ProductoService;
import api.service.ServerlessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProcesarProductoFunction procesarProductoFunction;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ServerlessService serverlessService;

    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        // Guardado inmediato en base de datos local
        Producto nuevoProducto = productoRepository.save(producto);

        // Disparar el proceso pesado en segundo plano (Dispara y olvida)
        serverlessService.sincronizarConCloudAsincrono(
                nuevoProducto.getNombre(),
                nuevoProducto.getPrecio(),
                nuevoProducto.getStock());

        // 3. Responder de inmediato al cliente (Postman)
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> modificar(@PathVariable Long id,
            @RequestBody ProductoDTO dto) {
        return ResponseEntity.ok(productoService.modificar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}