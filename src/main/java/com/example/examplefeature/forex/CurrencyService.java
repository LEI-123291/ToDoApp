package com.example.examplefeature.forex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

@Service
public class CurrencyService {

    private static final List<String> SYMBOLS =
            Arrays.asList("USD","GBP","JPY","CHF","BRL","AUD","CAD","CNY");

    // Frankfurter não precisa de chave e devolve sempre "rates"
    // Ex.: https://api.frankfurter.app/latest?from=EUR&to=USD,GBP
    private static final String API_URL =
            "https://api.frankfurter.app/latest?from=EUR&to=" + String.join(",", SYMBOLS);

    private final OkHttpClient http = new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(10))
            .connectTimeout(Duration.ofSeconds(5))
            .readTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    public Map<String, Double> getRates() throws IOException {
        Request req = new Request.Builder().url(API_URL).get().build();

        try (Response res = http.newCall(req).execute()) {
            if (!res.isSuccessful()) {
                throw new IOException("HTTP " + res.code() + " ao obter câmbios");
            }
            if (res.body() == null) {
                throw new IOException("Resposta sem corpo");
            }

            JsonNode root = mapper.readTree(res.body().byteStream());

            // Frankfurter: { "amount":1.0, "base":"EUR", "date":"2025-10-17", "rates":{...}}
            JsonNode rates = root.path("rates");
            if (rates.isMissingNode() || !rates.isObject()) {
                // fallback para mensagens de erro nativas se existirem
                String apiMsg = root.path("error").asText(root.path("message").asText("Resposta inválida (sem 'rates')"));
                throw new IOException(apiMsg);
            }

            Map<String, Double> out = new TreeMap<>();
            for (String s : SYMBOLS) {
                JsonNode node = rates.path(s);
                if (node.isNumber()) {
                    out.put(s, node.asDouble());
                }
            }
            return out;
        }
    }
}
