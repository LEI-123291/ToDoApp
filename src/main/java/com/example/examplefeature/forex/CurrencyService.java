package com.example.examplefeature.forex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class CurrencyService {
    // podes limitar símbolos para reduzir payload
    private static final List<String> SYMBOLS = Arrays.asList("USD","GBP","JPY","CHF","BRL","AUD","CAD","CNY");
    private static final String API = "https://api.exchangerate.host/latest?base=EUR&symbols=" + String.join(",", SYMBOLS);

    private final OkHttpClient http = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Double> getRates() throws IOException {
        Request req = new Request.Builder()
                .url(API)
                .header("User-Agent", "ToDoApp/1.0") // alguns serviços gostam disto
                .build();

        try (Response res = http.newCall(req).execute()) {
            if (!res.isSuccessful() || res.body() == null) {
                throw new IOException("HTTP " + res.code() + " ao obter taxas");
            }

            JsonNode root = mapper.readTree(res.body().byteStream());

            // Defensive parsing: usa 'path' para evitar null
            JsonNode rates = root.path("rates");
            if (rates.isMissingNode() || !rates.isObject()) {
                // algumas APIs devolvem erro em 'error'/'message'
                String apiMsg = root.path("error").asText(root.path("message").asText("Resposta inválida (sem 'rates')"));
                throw new IOException(apiMsg);
            }

            Map<String, Double> out = new TreeMap<>();
            for (String s : SYMBOLS) {
                JsonNode node = rates.path(s);
                if (node.isNumber()) out.put(s, node.asDouble());
            }
            return out;
        }
    }
}
