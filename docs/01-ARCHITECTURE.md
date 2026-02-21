# Arquitetura e Domain-Driven Design (DDD)

## Separação em Camadas

O projeto segue uma arquitetura em camadas inspirada em **Clean Architecture** e **Hexagonal Architecture (Ports & Adapters)**, com clara separação de responsabilidades:

```
com.banking.demo/
├── domain/           # Núcleo do negócio - regras e entidades
├── application/      # Casos de uso e interfaces (ports)
├── infrastructure/   # Implementações concretas (adapters)
└── interfaces/       # REST Controllers e DTOs
```

### Domain Layer (Domínio)

Contém as entidades de negócio e suas regras. **Não depende de frameworks** nem de infraestrutura.

| Classe | Responsabilidade |
|--------|------------------|
| `Account` | Entidade raiz com regras de débito/crédito e validações |
| `Transaction` | Registro imutável de movimentação |
| `OutboxEvent` | Entidade para o padrão Transactional Outbox |
| `TransferLog` | Rastreamento de passos para auditoria |

**Técnica aplicada:** Regras de negócio encapsuladas na entidade. Exemplo em `Account.debit()`:

```java
public void debit(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
        throw new IllegalArgumentException("Valor do débito deve ser positivo");
    if (balance.compareTo(amount) < 0)
        throw new IllegalStateException("Saldo insuficiente");
    this.balance = this.balance.subtract(amount);
}
```

- Uso de `BigDecimal` para precisão financeira
- `@Version` para **optimistic locking** e prevenção de condições de corrida
- `equals()`/`hashCode()` baseados em identidade

### Application Layer (Aplicação)

Define **casos de uso** e **portas (interfaces)** para persistência e mensageria. A camada de aplicação orquestra o domínio e define o que precisa ser feito, sem saber *como* será implementado.

**Ports (Interfaces):**
- `AccountRepository`, `TransactionRepository`, `OutboxRepository`, `TransferLogRepository`
- `EventPublisher` – abstração para publicação de eventos

**Services:**
- `AccountService` – operações sobre contas (débito, crédito, estorno)
- `TransferService` – orquestração dos 3 fluxos de transferência
- `OutboxRelayService` – worker que publica eventos da tabela outbox

### Infrastructure Layer (Infraestrutura)

Implementa as portas com tecnologias específicas:

| Componente | Implementação |
|------------|---------------|
| Repositories | `JpaAccountRepository`, `JpaTransactionRepository`, etc. |
| EventPublisher | `KafkaEventPublisher` |
| Kafka | `KafkaConfig`, `KafkaNativeTransferConsumer`, `SagaConsumer`, `CompensationConsumer` |
| Scheduler | `OutboxRelayScheduler` |

### Interfaces Layer

Adaptadores que expõem a aplicação via HTTP:

- `TransferController`, `AccountController`
- `GlobalExceptionHandler` – tratamento centralizado de exceções
- `WebConfig` – CORS e configurações web
- DTOs de request/response

## Inversão de Dependência

Os serviços dependem de **interfaces (ports)**, não de implementações concretas:

```java
public class AccountService {
    private final AccountRepository accountRepository;  // Interface
    
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
```

O Spring injeta `JpaAccountRepository` quando `AccountRepository` é solicitado, permitindo testes unitários com mocks e troca de implementação sem alterar o núcleo.

## Benefícios desta Arquitetura

- **Testabilidade:** Domínio e aplicação podem ser testados sem banco ou Kafka
- **Manutenibilidade:** Mudanças em JPA, Kafka ou REST ficam isoladas
- **Evolução:** Novos adaptadores (ex: eventos via RabbitMQ) sem impactar o domínio
