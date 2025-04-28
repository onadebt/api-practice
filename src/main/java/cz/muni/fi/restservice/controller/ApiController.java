package cz.muni.fi.restservice.controller;


import cz.muni.fi.restservice.service.AirportService;
import cz.muni.fi.restservice.service.EvalService;
import cz.muni.fi.restservice.service.StockService;
import cz.muni.fi.restservice.service.WeatherService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    private final WeatherService weatherService;
    private final StockService stockService;
    private final EvalService evalService;
    private final AirportService airportService;

    public ApiController(WeatherService weatherService,
                         StockService stockService,
                         EvalService evalService,
                         AirportService airportService) {
        this.weatherService = weatherService;
        this.stockService = stockService;
        this.evalService = evalService;
        this.airportService = airportService;
    }

    @GetMapping("/api")
    public ResponseEntity<?> root(@RequestParam(value = "queryAirportTemp", required = false) String airport,
                                  @RequestParam(value = "queryStockPrice", required = false) String stock,
                                  @RequestParam(value = "queryEval", required = false) String expr,
                                  @RequestParam(value = "format", required = false) String format,
                                  HttpServletRequest request) {
        int provided = (airport != null ? 1 : 0) + (stock != null ? 1 : 0) + (expr != null ? 1 : 0);
        if (provided != 1) {
            return ResponseEntity.badRequest().body("Exactly one query parameter must be provided.");
        }

        try {
            double result;
            if (airport != null) {
                var coords = airportService.getCoordinates(airport);
                result = weatherService.getTemperature(coords.get().lat(), coords.get().lon());
            } else if (stock != null) {
                result = stockService.getPrice(stock);
            } else {
                result = evalService.evaluate(expr);
            }

            MediaType contentType = chooseContentType(request, format);
            if (MediaType.APPLICATION_XML.equals(contentType)) {
                String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><result>" + result + "</result>";
                return ResponseEntity.ok().contentType(contentType).body(xml);
            }
            return ResponseEntity.ok().contentType(contentType).body(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + ex.getMessage());
        }
    }

    private MediaType chooseContentType(HttpServletRequest req, String formatParam) {
        if ("xml".equalsIgnoreCase(formatParam)) return MediaType.APPLICATION_XML;
        String accept = req.getHeader(HttpHeaders.ACCEPT);
        if (accept != null && accept.contains("xml")) return MediaType.APPLICATION_XML;
        return MediaType.APPLICATION_JSON;
    }
}
