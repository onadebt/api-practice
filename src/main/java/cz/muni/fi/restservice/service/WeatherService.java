package cz.muni.fi.restservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class WeatherService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public double getTemperature(double lat, double lon) throws IOException, InterruptedException {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&current_weather=true&temperature_unit=celsius";
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(resp.body());
        return root.path("current_weather").path("temperature").asDouble();
    }
}