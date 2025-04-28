package cz.muni.fi.restservice.service;

import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
public class EvalService {
    public double evaluate(String raw) {
        String expr = URLDecoder.decode(raw, StandardCharsets.UTF_8);
        expr = expr.replace(" ", "+");
        return new ExpressionBuilder(expr).build().evaluate();
    }
}