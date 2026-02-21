package com.banking.demo.interfaces.rest;

import com.banking.demo.application.service.TransferService;
import com.banking.demo.domain.TransferLog;
import com.banking.demo.domain.TransferType;
import com.banking.demo.interfaces.rest.dto.TransferLogResponse;
import com.banking.demo.interfaces.rest.dto.TransferRequest;
import com.banking.demo.interfaces.rest.dto.TransferResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/kafka-native")
    public ResponseEntity<TransferResponse> transferKafkaNative(@Valid @RequestBody TransferRequest request) {
        String correlationId = transferService.transferKafkaNative(
                request.getFromAccountId(), request.getToAccountId(), request.getAmount(), request.isForceError());
        return ResponseEntity.ok(new TransferResponse(correlationId));
    }

    @PostMapping("/saga")
    public ResponseEntity<TransferResponse> transferSaga(@Valid @RequestBody TransferRequest request) {
        String correlationId = transferService.transferSaga(
                request.getFromAccountId(), request.getToAccountId(), request.getAmount(), request.isForceError());
        return ResponseEntity.ok(new TransferResponse(correlationId));
    }

    @PostMapping("/outbox")
    public ResponseEntity<TransferResponse> transferOutbox(@Valid @RequestBody TransferRequest request) {
        String correlationId = transferService.transferOutbox(
                request.getFromAccountId(), request.getToAccountId(), request.getAmount(), request.isForceError());
        return ResponseEntity.ok(new TransferResponse(correlationId));
    }

    @GetMapping("/logs")
    public ResponseEntity<List<TransferLogResponse>> getLogs(@RequestParam String correlationId) {
        List<TransferLog> logs = transferService.getLogs(correlationId);
        List<TransferLogResponse> response = logs.stream()
                .map(log -> new TransferLogResponse(log.getStep(), log.getMessage(), log.getCreatedAt()))
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
