package com.banking.demo.infrastructure.kafka;

import com.banking.demo.application.dto.TransferCommand;
import com.banking.demo.application.service.AccountService;
import com.banking.demo.application.service.TransferService;
import com.banking.demo.domain.TransferType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class KafkaNativeTransferConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaNativeTransferConsumer.class);
    private static final BigDecimal ERROR_LIMIT = new BigDecimal("10000");

    private final AccountService accountService;
    private final TransferService transferService;

    public KafkaNativeTransferConsumer(AccountService accountService, TransferService transferService) {
        this.accountService = accountService;
        this.transferService = transferService;
    }

    @KafkaListener(topics = Topics.KAFKA_NATIVE_TRANSFER, groupId = "kafka-native-group")
    @Transactional
    public void process(TransferCommand command) {
        log.info("Processando transferência Kafka Native: {}", command.getCorrelationId());
        accountService.logStep(command.getCorrelationId(), 3, "Consumer recebeu evento", "KAFKA_NATIVE");

        boolean shouldFail = command.isForceError() || command.getAmount().compareTo(ERROR_LIMIT) > 0;
        if (shouldFail) {
            accountService.logStep(command.getCorrelationId(), 4, "Simulação de erro (valor > 10.000 ou Forçar erro) - Transferência rejeitada", "KAFKA_NATIVE");
            accountService.logStep(command.getCorrelationId(), 5, "Nenhum débito/crédito realizado", "KAFKA_NATIVE");
            return;
        }

        accountService.debit(command.getFromAccountId(), command.getAmount(), command.getCorrelationId(), TransferType.KAFKA_NATIVE);
        accountService.logStep(command.getCorrelationId(), 4, "Débito realizado", "KAFKA_NATIVE");

        accountService.credit(command.getToAccountId(), command.getAmount(), command.getCorrelationId(), TransferType.KAFKA_NATIVE);
        accountService.logStep(command.getCorrelationId(), 5, "Crédito realizado - Transferência concluída", "KAFKA_NATIVE");
    }
}
