package com.banking.demo.application.port;

import com.banking.demo.domain.OutboxEvent;

import java.util.List;

public interface OutboxRepository {

    OutboxEvent save(OutboxEvent event);

    List<OutboxEvent> findUnpublished(int limit);

    OutboxEvent saveAndFlush(OutboxEvent event);
}
