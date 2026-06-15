package api.service;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import api.model.Producto;

@Component

public class ProcesarProductoFunction {
   @Bean
    public Consumer<Producto> procesarSincronizacion() { // <-- Cambiado de ProductoDTO a Producto
        return producto -> {
            System.out.println("🚀 [Serverless FaaS] Sincronizando nuevo producto con inventario Cloud...");
            
            // Calculamos métricas del inventario basándonos en tu entidad
            double valorTotalStock = producto.getPrecio() * producto.getStock();
            double impuestoIVA = valorTotalStock * 0.19;

            System.out.println("Producto: " + producto.getNombre());
            System.out.println("Valor Stock: $" + valorTotalStock + " | IVA Estimado (19%): $" + impuestoIVA);
            System.out.println("Sincronización Serverless completada de forma asíncrona.");
        };
    }
}
