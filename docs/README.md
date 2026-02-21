# Documentação Técnica - Banking Demo

Esta documentação foi elaborada para apresentar as técnicas e conhecimentos aplicados no projeto **Banking Demo - Transações Distribuídas**, demonstrando competências relevantes para posições em desenvolvimento Java.

## Índice

| Documento | Conteúdo |
|-----------|----------|
| [01 - Arquitetura e DDD](01-ARCHITECTURE.md) | Domain-Driven Design, Clean Architecture, Separação de Camadas |
| [02 - Padrões de Transações Distribuídas](02-DISTRIBUTED-PATTERNS.md) | Saga, Transactional Outbox, Kafka Native Transactions |
| [03 - Competências Java e Spring](03-JAVA-SPRING-SKILLS.md) | Spring Boot, JPA, Kafka, Virtual Threads, Injeção de Dependência |
| [04 - Qualidade e Resiliência](04-QUALITY-AND-RESILIENCE.md) | Tratamento de Erros, Validação, Idempotência, Retry |

## Visão Geral do Projeto

O projeto implementa três estratégias distintas para transferências entre contas bancárias, servindo como demonstração prática de:

- **Consistência eventual** vs **atômica** em sistemas distribuídos
- **Compensação** de transações em cenários de falha
- **Integração** entre banco de dados relacional e mensageria (Kafka)

## Tecnologias Utilizadas

- **Backend:** Java 17, Spring Boot 3.5, Spring Kafka, Spring Data JPA
- **Frontend:** Vue.js 3, Tailwind CSS
- **Infraestrutura:** Docker, PostgreSQL, Apache Kafka (KRaft)
