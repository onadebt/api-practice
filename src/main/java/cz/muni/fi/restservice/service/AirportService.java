package cz.muni.fi.restservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
public class AirportService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public Optional<Coord> getCoordinates(String iata) {
        var url = "https://airport-data.com/api/ap_info.json?iata=" + iata.toUpperCase();
        var req = HttpRequest.newBuilder().uri(URI.create(url)).build();
        try {
            var resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            var node = mapper.readTree(resp.body());
            if (node.hasNonNull("latitude") && node.hasNonNull("longitude")) {
                return Optional.of(new Coord(node.get("latitude").asDouble(), node.get("longitude").asDouble()));
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error fetching coordinates for IATA code: " + iata, e);
        }
        throw new IllegalArgumentException("Unknown IATA code: " + iata);
    }

    public record Coord(double lat, double lon) {
    }
}
