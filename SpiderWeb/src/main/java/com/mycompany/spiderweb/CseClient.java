package com.mycompany.spiderweb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.mycompany.spiderweb.Models.*;

public class CseClient {

    private final SettingsStore settings;
    private final ObjectMapper mapper = new ObjectMapper();

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(12))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public CseClient(SettingsStore settings) {
        this.settings = settings;
    }

    public SearchResponse search(String query, int num, int start) throws Exception {
        int safeNum = Math.min(Math.max(num, 1), 10);
        int safeStart = Math.max(1, start);

        String url = "https://www.googleapis.com/customsearch/v1"
                + "?key=" + URLEncoder.encode(settings.apiKey(), StandardCharsets.UTF_8)
                + "&cx="  + URLEncoder.encode(settings.cx(), StandardCharsets.UTF_8)
                + "&q="   + URLEncoder.encode(query, StandardCharsets.UTF_8)
                + "&num=" + safeNum
                + "&start=" + safeStart
                + "&hl=vi&gl=vn&safe=active";

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(18))
                .header("User-Agent", "Mozilla/5.0 JavaHttpClient")
                .GET()
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        JsonNode root = mapper.readTree(resp.body());

        JsonNode err = root.path("error");
        if (!err.isMissingNode() && err.has("message")) {
            throw new RuntimeException(err.path("message").asText());
        }
        if (resp.statusCode() >= 400) {
            throw new RuntimeException("HTTP " + resp.statusCode() + " — " + resp.body());
        }

        long total = 0;
        try {
            total = Long.parseLong(root.path("searchInformation").path("totalResults").asText("0"));
        } catch (Exception ignored) {}

        List<ResultItem> out = new ArrayList<>();
        JsonNode items = root.path("items");
        if (items.isArray()) {
            for (JsonNode it : items) {
                String title = it.path("title").asText("");
                String link = it.path("link").asText("");
                String snippet = it.path("snippet").asText("");
                String displayLink = it.path("displayLink").asText(Utils.hostOf(link));
                if (!link.isBlank()) out.add(new ResultItem(title, link, snippet, displayLink));
            }
        }

        return new SearchResponse(total, out);
    }
}
