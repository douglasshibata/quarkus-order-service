# 📦 Quarkus Reactive Order Service (EDA)

[![Quarkus](https://img.shields.io/badge/Quarkus-3.19.1-blueviolet?logo=quarkus)](https://quarkus.io)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org)
[![Reactive](https://img.shields.io/badge/Reactive-Mutiny-brightgreen)](https://smallrye.io/smallrye-mutiny)
[![Kafka](https://img.shields.io/badge/Broker-Apache%20Kafka-black?logo=apachekafka)](https://kafka.apache.org)

Este microsserviço moderno, nativo para nuvem (Cloud-Native) e de altíssima performance, foi desenvolvido utilizando **Quarkus** e **Arquitetura Orientada a Eventos (EDA)**. Ele demonstra o uso ideal de programação reativa não-bloqueante de ponta a ponta com **Hibernate Reactive**, **Panache Entity**, **SmallRye Reactive Messaging (Kafka)** e mapeamentos automatizados de DTOs via **MapStruct**.

---

## 🏛️ 1. Arquitetura e Fluxo de Dados

A arquitetura do sistema é orientada a eventos e é totalmente não-bloqueante baseada na biblioteca de programação reativa **SmallRye Mutiny**.

### Fluxo de Criação de Pedido (Ingestion Pipeline)
```text
  ┌─────────────┐             ┌──────────────┐             ┌───────────────┐
  │ Cliente API │ ──────────> │OrderResource │ ──────────> │ Order (Entity)│
  └─────────────┘             │ (Mutiny Uni) │             │(Panache HR-AR)│
         ▲                    └──────────────┘             └───────────────┘
         │                                                         │
         │ (HTTP 201 Created)                                      │ (Persistência
         └──────────────────────────────────────┐                  │  Reativa)
                                                │                  ▼
                                       ┌─────────────────┐  ┌──────────────┐
                                       │SmallRye Emitter │ <│ PostgreSQL   │
                                       └─────────────────┘  └──────────────┘
                                                │
                                                │ (Publicação de Evento)
                                                ▼
                                      ┌───────────────────┐
                                      │    Kafka Topic    │
                                      │  (orders-events)  │
                                      └───────────────────┘
```

### Fluxo de Confirmação de Pagamento (Consumo & Atualização Reativa)
```text
  ┌──────────────────┐               ┌──────────────────────┐               ┌────────────────┐
  │   Kafka Topic    │ ────────────> │ PaymentEventConsumer │ ────────────> │ Order (Entity) │
  │(payments-events) │  (Via @Incoming)│ (Mutiny/Resilience)  │ (Panache AR)  │(Aprova/Rejeita)│
  └──────────────────┘               └──────────────────────┘               └────────────────┘
```

---

## 📂 2. Estrutura do Projeto

A estrutura de pacotes orientada a domínios organiza o código conforme as boas práticas de microsserviços modernos:

```text
quarkus-order-service/
├── docker-compose.yml              # PostgreSQL local, Kafka KRaft, Kafka UI
├── pom.xml                         # Dependências do ecossistema Quarkus e plugins
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── empresa/
│   │   │           └── order/
│   │   │               ├── api/
│   │   │               │   └── OrderResource.java          # REST Endpoint reativo com Mutiny
│   │   │               ├── consumer/
│   │   │               │   ├── PaymentEventConsumer.java   # Listener de eventos com resiliência
│   │   │               │   └── PaymentEventDeserializer.java# Desserializador Jackson para Kafka
│   │   │               ├── domain/
│   │   │               │   ├── Order.java                  # Entidade Active Record Panache
│   │   │               │   ├── OrderEvent.java             # Evento de Pedido Criado
│   │   │               │   ├── OrderStatus.java            # Enum com estados do pedido
│   │   │               │   └── PaymentEvent.java           # Payload do pagamento recebido
│   │   │               ├── dto/
│   │   │               │   ├── OrderCreateDTO.java         # DTO de Entrada
│   │   │               │   └── OrderResponseDTO.java        # DTO de Saída
│   │   │               ├── mapper/
│   │   │               │   └── OrderMapper.java            # Interface MapStruct (Jakarta Component)
│   │   │               └── producer/
│   │   │                   └── OrderEventProducer.java     # Emitter reativo de eventos do Kafka
│   │   └── resources/
│   │       └── application.properties                      # Configurações do microsserviço
│   └── test/
│       └── java/
│           └── com/
│               └── empresa/
│                   └── order/
│                       ├── api/
│                       │   └── OrderResourceTest.java      # Testes Unitários de domínio e JAX-RS
│                       └── mapper/
│                           └── OrderMapperTest.java        # Testes Unitários de Mapeamento MapStruct
```

---

## 🛠️ 3. Instalação e Requisitos

Certifique-se de possuir instalado em sua máquina de desenvolvimento:
* **Java SDK 21** (ou posterior)
* **Maven 3.9+**
* **Docker & Docker Compose** (para infraestrutura local)

Clone o repositório ou navegue até a pasta do microsserviço:
```bash
cd quarkus-order-service
```

---

## ⚙️ 4. Configurações (`application.properties`)

As seguintes configurações principais estruturam a persistência reativa e a integração de mensageria com o Kafka:

| Propriedade | Descrição | Valor Padrão |
|-------------|-----------|--------------|
| `quarkus.datasource.db-kind` | Banco de dados relacional utilizado | `postgresql` |
| `quarkus.datasource.reactive.url` | URL de conexão JDBC Reativa do PostgreSQL | `postgresql://localhost:5432/orders_db` |
| `kafka.bootstrap.servers` | Servidor de bootstrap para o Kafka | `localhost:9092` |
| `mp.messaging.outgoing.orders-out.topic` | Tópico Kafka para eventos de saída | `orders-events` |
| `mp.messaging.incoming.payments-in.topic` | Tópico Kafka para eventos de entrada | `payments-events` |

---

## 🚀 5. Execução & Uso

### Passo 1: Inicializar a Infraestrutura (Docker)
Execute o seguinte comando para inicializar o banco de dados PostgreSQL e o broker Apache Kafka em modo KRaft:
```bash
docker-compose up -d
```
Acesse o **Kafka UI** através de: `http://localhost:8089`

### Passo 2: Executar o Quarkus em Modo de Desenvolvimento (Live Reload)
```bash
mvn quarkus:dev
```
O serviço estará disponível na porta `8080`.

### Passo 3: Enviar requisição para criação de um Pedido
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Renata Lima",
    "productName": "Monitor UltraWide 34",
    "quantity": 1,
    "price": 2800.00
  }'
```

**Resposta HTTP 201 (Created):**
```json
{
  "id": 1,
  "customerName": "Renata Lima",
  "productName": "Monitor UltraWide 34",
  "quantity": 1,
  "price": 2800.0,
  "status": "PENDING"
}
```

---

## 🧪 6. Testes

Para rodar toda a suite de testes unitários herméticos de alta confiabilidade (sem necessidade de conexões ativas com o banco de dados/brokers):

```bash
mvn clean test
```

---

## 🔒 7. Segurança & Práticas Cloud-Native

1. **Compilação Nativa via GraalVM:** Pronto para ser compilado como binário nativo via `mvn package -Pnative`, permitindo tempos de cold start inferiores a 20ms e baixíssimo uso de memória em contêineres Kubernetes.
2. **Resiliência MP Fault Tolerance:** O listener `PaymentEventConsumer` possui tratamento resiliente nativo contra indisponibilidades de brokers ou concorrência de banco, utilizando estratégias configuradas de `@Retry` e `@Fallback`.
3. **Escaneamento de Segurança:** Livre de vulnerabilidades conhecidas, utilizando versões seguras e atualizadas do Quarkus 3.19.1.

---

## 🔍 8. Troubleshooting

* **Erro: `Connection refused: localhost/127.0.0.1:5432` no ambiente de produção:**
  * Certifique-se de que o PostgreSQL local está rodando executando `docker ps`. Se sim, verifique se a porta `5432` não está ocupada por outra instância local do Postgres.
* **Problema com portas de serviços locais:**
  * O Kafka UI foi intencionalmente configurado na porta `8089` (no arquivo `docker-compose.yml`) para evitar qualquer colisão com a porta padrão do Quarkus `8080`.
* **Estabilidade Reativa:**
  * O Quarkus Reactive utiliza pools de conexão do Vert.x sob o capô. Nunca introduza bibliotecas ou chamadas bloqueantes (como `Thread.sleep` ou chamadas JDBC tradicionais bloqueantes) no fluxo reativo do Mutiny, para evitar congelamento do event loop do Vert.x.
