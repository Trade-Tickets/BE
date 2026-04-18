package com.royce.hackerhouse.controller;

import com.royce.hackerhouse.dto.PriceResponse;
import com.royce.hackerhouse.service.OracleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/oracle")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OracleController {

    private final OracleService oracleService;

    @GetMapping("/sui-price")
    public PriceResponse getSuiPrice() {
        return oracleService.getLatestPrice();
    }
}
