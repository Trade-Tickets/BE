package com.royce.hackerhouse.controller;

import com.royce.hackerhouse.dto.WalletDtos.RecordTradeRequest;
import com.royce.hackerhouse.dto.WalletDtos.TradeRecordResponse;
import com.royce.hackerhouse.dto.WalletDtos.WalletProfileResponse;
import com.royce.hackerhouse.service.WalletTradeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletTradeService walletTradeService;

    @GetMapping("/{walletAddress}")
    public WalletProfileResponse getWalletProfile(@PathVariable String walletAddress) {
        return walletTradeService.getWalletProfile(walletAddress);
    }

    @GetMapping("/{walletAddress}/trades")
    public List<TradeRecordResponse> getWalletTrades(@PathVariable String walletAddress) {
        return walletTradeService.getWalletTrades(walletAddress);
    }

    @PostMapping("/trades")
    @ResponseStatus(HttpStatus.CREATED)
    public TradeRecordResponse recordTrade(@RequestBody RecordTradeRequest request) {
        return walletTradeService.recordTrade(request);
    }
}
