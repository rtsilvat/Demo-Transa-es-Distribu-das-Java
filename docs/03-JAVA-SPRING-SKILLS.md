# Competências Java e Spring Demonstradas

## Java

### Versionamento e Tipos

- **Java 17** – Records, pattern matching, text blocks (onde aplicável)
- **BigDecimal** para valores monetários
- **Optional** em repositórios para evitar null
- Uso de **imutabilidade** em DTOs e entidades quando apropriado

### Boas Práticas

- **Encapsulamento** – regras no domínio, não espalhadas em controllers
- **Construtor protegido** em entidades JPA para framework
- **equals/hashCode** baseados em identidade (id)

---

## Spring Boot

### Configuração

- `application.yml` com profiles e externalização de configuração
- Propriedades para datasource, JPA, Kafka
- **Virtual Threads** habilitadas: `spring.threads.virtual.enabled: true`

### Spring Data JPA

- Repositories estendendo `JpaRepository` e interfaces de domínio
- Derived queries: `findByAccountIdOrderByCreatedAtDesc`, `findByCorrelationIdOrderByStepAsc`
- `@Query` para consultas personalizadas (ex.: outbox não publicados)
- `Pageable` para paginação
- Uso de `@Version` para controle otimista

### Spring Kafka

- `KafkaTemplate` com `executeInTransaction` para transações
- `@KafkaListener` para consumers
- `JsonSerializer`/`JsonDeserializer` para mensagens JSON
- `transaction-id-prefix` para exactly-once
- Configuração de tópicos via `@Bean NewTopic`

### Transações

- `@Transactional` com `rollbackFor` quando necessário
- Múltiplos `TransactionManager` (JPA e Kafka) com `@Primary` no JPA
- `readOnly = true` em operações de leitura

### Validação

- `@Valid` em controllers
- `@NotNull`, `@DecimalMin` em DTOs
- `MethodArgumentNotValidException` tratada no `GlobalExceptionHandler`

---

## Injeção de Dependência

Uso consistente de **injeção via construtor**:

```java
public class TransferService {
    private final AccountService accountService;
    private final EventPublisher eventPublisher;

    public TransferService(AccountService accountService, EventPublisher eventPublisher) {
        this.accountService = accountService;
        this.eventPublisher = eventPublisher;
    }
}
```

Benefícios: dependências explícitas, imutabilidade, facilitam testes e evita `@Autowired` em campos.

---

## Spring Retry

- `@Retryable` em operações de publicação no Kafka
- `@Recover` para fallback em falhas
- `@EnableRetry` na aplicação

---

## Agendamento

- `@Scheduled(fixedDelay = 2000)` no `OutboxRelayScheduler`
- `@EnableScheduling` na aplicação
