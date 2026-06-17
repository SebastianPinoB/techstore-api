package api.serverless.lambda;

import java.util.Map;

public class StockLambda {
    public Map<String, Object> handleRequest(Map<String, Object> input) {

        double precio =
                Double.parseDouble(input.get("precio").toString());

        int stock =
                Integer.parseInt(input.get("stock").toString());

        double iva = precio * 0.19;
        double valorTotalStock = (precio + iva) * stock;

        return Map.of(
                "precio", precio,
                "stock", stock,
                "iva", iva,
                "valorTotalStock", valorTotalStock
        );
    }
}
