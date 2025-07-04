# Messaging Protocols and Kakfa Demo

A comprehensive Spring Boot application demonstrating 5 different messaging protocols and Kafka in action.

Why was this done:

- All six solve messaging/communication problems
- Developers often need to choose between them for different scenarios

## Protocols and Kafka Demonstrated

### 1. **AMQP (Advanced Message Queuing Protocol)**
- **Implementation**: RabbitMQ
- **Use Case**: Enterprise message broker for reliable, transactional messaging
- **Features**: Queue-based messaging, routing, durability, acknowledgments

### 2. **MQTT (Message Queuing Telemetry Transport)**
- **Implementation**: Eclipse Paho Client + Mosquitto Broker
- **Use Case**: IoT devices, lightweight pub/sub messaging
- **Features**: Lightweight, quality of service levels, retained messages

### 3. **STOMP (Simple Text Oriented Messaging Protocol)**
- **Implementation**: Spring WebSocket + SockJS
- **Use Case**: WebSocket-based messaging for web applications
- **Features**: Text-based protocol, web-friendly, real-time communication

### 4. **CoAP (Constrained Application Protocol)**
- **Implementation**: Eclipse Californium
- **Use Case**: IoT devices with limited resources, RESTful API for sensors
- **Features**: UDP-based, REST-like, optimized for constrained devices

### 5. **XMPP (Extensible Messaging and Presence Protocol)**
- **Implementation**: Smack Client + Prosody Server
- **Use Case**: Real-time chat, presence, instant messaging
- **Features**: XML-based, presence information, federated architecture

### 6. **Apache Kafka**
- **Implementation**: Spring Kafka
- **Use Case**: High-throughput event streaming, log aggregation
- **Features**: Distributed, high-throughput, fault-tolerant, persistent

## Running the Application (Requires Java 17)

### 1. Start Infrastructure Services
**Required** - Download Docker Desktop from https://www.docker.com/products/docker-desktop/ 
```bash
docker-compose up -d
```
```bash
docker-compose down -v
```

This starts:
- RabbitMQ (AMQP) on port 5672
- Mosquitto (MQTT) on port 1883
- Prosody (XMPP) on port 5222
- Kafka + Zookeeper on port 9092

### 2. Run the Spring Boot Application
```bash
mvn spring-boot:run
```

### 3. Access the Web UI
Open your browser and navigate to: `http://localhost:8080`

## Testing the Protocols and Kafka

### Web Interface
- Use the web interface at `http://localhost:8080` to send messages through each protocol
- Messages are displayed in real-time as they're received
- Each section shows its use case and functionality

### REST API Endpoints
All protocols and Kafka can be tested via REST endpoints:

```bash
# AMQP
curl -X POST http://localhost:8080/api/messaging/amqp/send \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello AMQP"}'

# MQTT
curl -X POST http://localhost:8080/api/messaging/mqtt/send \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello MQTT"}'

# STOMP
curl -X POST http://localhost:8080/api/messaging/stomp/send \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello STOMP"}'

# CoAP
curl -X POST http://localhost:8080/api/messaging/coap/send \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello CoAP"}'

# XMPP
curl -X POST http://localhost:8080/api/messaging/xmpp/send \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello XMPP"}'

# Kafka
curl -X POST http://localhost:8080/api/messaging/kafka/send \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello Kafka"}'

# Get all received messages
curl http://localhost:8080/api/messaging/messages
```
```bash 
#For a Windows Laptop:

# AMQP
curl -X POST -H "Content-Type: application/json" -d "{\"message\": \"Hello AMQP\"}" http://localhost:8080/api/messaging/amqp/send

# MQTT
curl -X POST -H "Content-Type: application/json" -d "{\"message\": \"Hello MQTT\"}" http://localhost:8080/api/messaging/mqtt/send

# STOMP
curl -X POST -H "Content-Type: application/json" -d "{\"message\": \"Hello STOMP\"}" http://localhost:8080/api/messaging/stomp/send

# CoAP
curl -X POST -H "Content-Type: application/json" -d "{\"message\": \"Hello CoAP\"}" http://localhost:8080/api/messaging/coap/send

# XMPP
curl -X POST -H "Content-Type: application/json" -d "{\"message\": \"Hello XMPP\"}" http://localhost:8080/api/messaging/xmpp/send

# Kafka
curl -X POST -H "Content-Type: application/json" -d "{\"message\": \"Hello Kafka\"}" http://localhost:8080/api/messaging/kafka/send

# Get all received messages
curl http://localhost:8080/api/messaging/messages
```

## Architecture Overview

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Web UI        │    │   REST API       │    │   Messaging     │
│   (Thymeleaf)   │◄──►│   Controller     │◄──►│   Services      │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                                        │
                               ┌────────────────────────┼─────────────────────┐
                               │                        │                     │
                    ┌──────────▼──────────┐  ┌─────────▼─────────┐  ┌─────────▼─────────┐
                    │     AMQP/MQTT       │  │   STOMP/CoAP      │  │   XMPP/Kafka      │
                    │     Services        │  │   Services        │  │   Services        │
                    └─────────────────────┘  └───────────────────┘  └───────────────────┘
                               │                       │                      │
                    ┌──────────▼──────────┐  ┌─────────▼─────────┐  ┌─────────▼─────────┐
                    │  RabbitMQ/Mosquitto │  │ WebSocket/CoAP Srv│  │ Prosody/Kafka Srv │
                    │     (Docker)        │  │   (Embedded)      │  │    (Docker)       │
                    └─────────────────────┘  └───────────────────┘  └───────────────────┘
Note: The / here represents two different items and corresponding 2 different servers, for space constraint, it has been
combined.
```

## Key Features

- **Unified Message Structure**: All protocols and Kafka use the same `MessageDto` for consistency
- **Real-time Web UI**: Interactive interface to test all protocols and Kafka
- **Minimal Dependencies**: Focused on core functionality without unnecessary complexity
- **Docker Integration**: Easy setup of required message brokers
- **Comprehensive Logging**: Detailed logs for debugging and monitoring
- **REST API**: Programmatic access to all messaging functionality

## Protocol & Kafka Comparison

| Protocol | Transport | Message Format | Use Case |
|----------|-----------|----------------|----------|
| AMQP | TCP | Binary | Enterprise messaging |
| MQTT | TCP | Binary | IoT, mobile |
| STOMP | TCP/WebSocket | Text | Web applications |
| CoAP | UDP | Binary | IoT, constrained devices |
| XMPP | TCP | XML | Chat, presence |
| Kafka | Kafka Protocol (Custom TCP Binary) | Binary | Event streaming |

## Technologies Used
- **Java 17**
- **Spring Boot 3.4.6** - Application framework
- **Spring AMQP** - RabbitMQ integration
- **Eclipse Paho** - MQTT client
- **Spring WebSocket** - STOMP messaging
- **Eclipse Californium** - CoAP implementation
- **Smack** - XMPP client library
- **Spring Kafka** - Kafka integration
- **Lombok** - Boilerplate code reduction
- **Thymeleaf** - Web templating
- **Docker Compose** - Infrastructure orchestration

### Note: 
Each protocol and Kafka's implementation is kept minimal but functional, making it easy to understand 
the core concepts without getting lost in complex configurations.