package api.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ServerlessService {
   @Async
   public void sincronizarConCloudAsincrono(String nombreProducto, double precio, int stock) {
        try {
            // Simulamos que la llamada a la función serverless tarda 3 segundos
            Thread.sleep(3000); 
            
            double iva = precio * 0.19;
            double valorTotalStock = (precio + iva) * stock;

            System.out.println("\n--------------------------------------------------------------");
            System.out.println("[Serverless FaaS] Sincronización Cloud completada en segundo plano.");
            System.out.println("Producto: " + nombreProducto);
            System.out.println("Valor Total Stock (con IVA 19%): $" + valorTotalStock);
            System.out.println("--------------------------------------------------------------\n");

        } catch (InterruptedException e) {
            System.err.println("Error en el hilo asíncrono: " + e.getMessage());
        }
    }
}
