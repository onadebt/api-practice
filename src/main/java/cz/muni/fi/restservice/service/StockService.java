package cz.muni.fi.restservice.service;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class StockService {
    private final HttpClient client = HttpClient.newHttpClient();

    public double getPrice(String symbol) throws Exception {
        String qSymbol = symbol.matches("^[A-Za-z]{1,4}$")
                ? symbol.toUpperCase() + ".US"
                : symbol.toUpperCase();

        String url = "https://stooq.com/q/l/?s=" + qSymbol + "&f=sd2t2ohlcv&h&e=csv";
        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder().uri(URI.create(url)).build(),
                HttpResponse.BodyHandlers.ofString());

        try (CSVReader reader = new CSVReader(new StringReader(resp.body()))) {
            reader.skip(1);
            String[] row = reader.readNext();
            if (row == null || row.length < 7 || row[6].equalsIgnoreCase("N/D") || row[6].equalsIgnoreCase("N/A")) {
                throw new IllegalArgumentException("Price unavailable for " + symbol);
            }
            return Double.parseDouble(row[6]);
        }
    }
}