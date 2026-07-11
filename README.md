# Tekken Analytics

**Tekken Analytics**는 철권 8 경기 리플레이를 수집하고, 플레이어 및 캐릭터 통계를 실시간으로 집계하여 조회할 수 있는 백엔드 프로젝트입니다.

단순히 경기 데이터를 저장하는 데 그치지 않고, 대량의 리플레이 데이터를 효율적으로 처리하기 위해 **RabbitMQ 기반 비동기 처리**, **메모리 집계(Aggregation)**, **Batch UPSERT**를 적용하여 데이터베이스 부하를 최소화하는 구조를 구현했습니다.

프로젝트는 다음과 같은 목표를 가지고 개발되었습니다.

* 철권 8 리플레이 데이터를 안정적으로 수집하고 저장
* 플레이어 전적 및 최근 경기 기록 제공
* 캐릭터별 승률과 랭킹 제공
* 캐릭터 간 상성 통계 제공
* 대량의 리플레이를 효율적으로 처리할 수 있는 아키텍처 구현

---

## Key Features

* **Replay Collection**

    * 외부 API로부터 리플레이 데이터를 주기적으로 수집

* **Asynchronous Processing**

    * RabbitMQ를 이용하여 데이터 수집과 저장을 분리
    * Producer/Consumer 구조를 통해 처리량 향상

* **Batch Aggregation**

    * 메모리에서 통계를 집계한 뒤 Batch UPSERT 수행
    * 데이터베이스 접근 횟수를 줄여 처리 성능 향상

* **Statistics**

    * 플레이어 전적 조회
    * 최근 경기 조회
    * 캐릭터 승률 및 랭킹
    * 캐릭터 상성 통계

본 프로젝트는 **대용량 데이터 처리와 성능 최적화**에 초점을 맞춘 백엔드 프로젝트이며, 실제 운영 환경을 고려한 비동기 처리와 배치 저장 구조를 설계하고 구현하는 것을 목표로 했습니다.

---

## Tech Stack

| Category          | Technology                       | Description                   |
| ----------------- | -------------------------------- | ----------------------------- |
| Language          | Java 21                          | 최신 LTS 버전 기반 개발               |
| Framework         | Spring Boot 4.1                  | REST API 및 애플리케이션 구성          |
| Persistence       | Spring Data JPA, JdbcClient      | JPA 기반 CRUD와 Batch SQL 처리     |
| Database          | PostgreSQL                       | 통계 데이터 저장 및 UPSERT 활용         |
| Messaging         | RabbitMQ                         | 비동기 이벤트 처리                    |
| API Documentation | SpringDoc OpenAPI (Swagger)      | REST API 문서 자동 생성             |
| Build Tool        | Gradle                           | 프로젝트 빌드 및 의존성 관리              |
| Container         | Docker, Docker Compose           | 개발 환경 구성                      |
| Testing           | JUnit 5, Mockito, Testcontainers | 단위 테스트 및 PostgreSQL 기반 통합 테스트 |

### Why These Technologies?

#### Spring Boot

애플리케이션 구성과 REST API 개발을 위해 사용했습니다. Spring Data JPA와 Validation, RabbitMQ 등 다양한 모듈을 쉽게 통합할 수 있다는 장점이 있습니다.

#### PostgreSQL

`ON CONFLICT`, `RETURNING` 등 PostgreSQL의 기능을 활용하여 Batch UPSERT를 구현했습니다. 이를 통해 대량 데이터 저장 시 불필요한 조회를 줄이고 저장 성능을 향상시켰습니다.

#### RabbitMQ

리플레이 수집과 통계 계산을 분리하기 위해 메시지 브로커를 도입했습니다. 데이터 수집 속도와 통계 처리 속도를 독립적으로 운영할 수 있도록 설계했습니다.

#### JdbcClient

일반적인 조회는 JPA를 사용하고, 대량 INSERT 및 Batch UPSERT는 JdbcClient를 사용했습니다. 이를 통해 ORM이 비효율적인 영역에서는 직접 SQL을 작성하여 성능을 최적화했습니다.

#### Testcontainers

Repository 통합 테스트에서 실제 PostgreSQL 컨테이너를 사용하여 운영 환경과 동일한 SQL 동작을 검증했습니다.

---

## System Architecture

### Overall Architecture

```text
                        +----------------------+
                        |      Scheduler       |
                        +----------+-----------+
                                   |
                                   v
                        +----------------------+
                        |  Replay Collector    |
                        +----------+-----------+
                                   |
                                   v
                        +----------------------+
                        |   RabbitMQ Producer  |
                        +----------+-----------+
                                   |
                                   v
                 +--------------------------------------+
                 |              RabbitMQ                |
                 +----------------+---------------------+
                                  |
                                  v
                      +--------------------------+
                      |     Replay Consumer      |
                      +------------+-------------+
                                   |
          +------------------------+------------------------+
          |                         |                        |
          v                         v                        v
+----------------------+  +----------------------+  +----------------------+
| Replay Persistence   |  | Character Statistics |  | Character Matchup    |
|     Aggregator       |  |      Aggregator      |  |      Aggregator      |
+----------+-----------+  +----------+-----------+  +----------+-----------+
           |                         |                         |
           v                         v                         v
+----------------------+   Batch UPSERT              Batch UPSERT
| ReplayPersistence    |          │                         │
|      Service         |          │                         │
+----------+-----------+          └───────────┬─────────────┘
           |                                  │
           └────── Batch INSERT / UPSERT ─────┘
                          │
                          ▼
                   +------------------+
                   |    PostgreSQL    |
                   +------------------+
```

---

### Data Flow

#### 1. Replay Collection

Scheduler가 주기적으로 실행되어 외부 API에서 최신 리플레이 데이터를 수집합니다.

수집된 데이터는 즉시 데이터베이스에 저장하지 않고 RabbitMQ로 전달하여 수집과 저장 과정을 분리했습니다.

---

#### 2. Asynchronous Processing

Replay Consumer는 RabbitMQ에서 메시지를 소비하며 다음 작업을 수행합니다.

* 경기(Match) 저장
* 플레이어(Player) 저장
* 경기 참가자(Match Participant) 저장
* 캐릭터 통계 집계
* 캐릭터 상성 집계

이를 통해 데이터 수집 속도와 통계 계산 속도를 서로 독립적으로 유지할 수 있습니다.

---

#### 3. Batch Aggregation

리플레이가 들어올 때마다 즉시 데이터베이스를 갱신하지 않습니다.

먼저 메모리에서 통계를 누적한 뒤 일정 개수 이상이 모이거나 일정 시간이 지나면 Batch UPSERT를 수행합니다.

이 방식은 데이터베이스 접근 횟수를 크게 줄이고 대량 데이터 처리 성능을 향상시킵니다.

---

#### 4. Persistence Strategy

프로젝트에서는 저장 대상에 따라 서로 다른 전략을 사용했습니다.

| Data                 | Strategy                                |
| -------------------- | --------------------------------------- |
| Match                | Batch INSERT (`ON CONFLICT DO NOTHING`) |
| Player               | Batch UPSERT (`ON CONFLICT DO UPDATE`)  |
| Match Participant    | Batch INSERT                            |
| Character Statistics | Batch UPSERT                            |
| Character Matchup    | Batch UPSERT                            |

이를 통해 중복 저장을 방지하면서도 필요한 데이터는 최신 상태로 유지하도록 설계했습니다.

---

### Design Goals

이 프로젝트의 아키텍처는 다음 목표를 중심으로 설계되었습니다.

* 데이터 수집과 저장을 분리하여 처리량 향상
* 대량 리플레이 처리 시 데이터베이스 부하 최소화
* 중복 저장 방지 및 데이터 무결성 보장
* Batch Processing을 통한 쓰기 성능 최적화
* 통계 조회 시 즉시 응답 가능한 구조 제공

---

## Performance Optimization

프로젝트를 구현하는 과정에서 가장 중점을 둔 부분은 **데이터베이스 쓰기 성능 개선**이었습니다.

초기 구현은 리플레이 한 건마다 여러 번의 INSERT와 UPDATE가 발생하는 구조였기 때문에, 데이터가 많아질수록 데이터베이스 부하가 빠르게 증가하는 문제가 있었습니다.

이를 해결하기 위해 Batch Processing과 Aggregation을 적용하여 데이터베이스 접근 횟수를 최소화했습니다.

---

### 1. Batch Persistence

#### Before

리플레이 한 건마다 각각의 엔티티를 개별 저장했습니다.

```text
Replay
 ├─ INSERT Match
 ├─ UPSERT Player #1
 ├─ UPSERT Player #2
 └─ INSERT MatchParticipant × 2
```

리플레이 수가 증가할수록 데이터베이스 호출도 선형적으로 증가했습니다.

---

#### After

여러 리플레이를 메모리에서 수집한 뒤 Batch SQL을 한 번에 수행하도록 변경했습니다.

```text
Replay × N
        │
        ▼
Collect Context
        │
        ▼
Batch INSERT Match
        │
        ▼
Batch UPSERT Player
        │
        ▼
Batch INSERT MatchParticipant
```

이를 통해 네트워크 왕복 횟수와 SQL 실행 횟수를 크게 줄일 수 있었습니다.

---

### 2. Batch Aggregation

캐릭터 통계와 상성 통계는 리플레이가 들어올 때마다 즉시 UPDATE하지 않고 메모리에서 먼저 집계합니다.

```text
Replay
    │
    ▼
Aggregation Buffer
    │
    ├─ Character Statistics
    └─ Character Matchups
    │
    ▼
Batch UPSERT
```

Aggregator는 일정 개수 이상의 데이터가 누적되거나 일정 시간이 지나면 한 번에 데이터베이스에 반영합니다.

이 방식은 동일한 캐릭터에 대한 UPDATE가 반복적으로 발생하는 문제를 줄여주며, 대량 리플레이 처리 시 데이터베이스 부하를 크게 감소시킵니다.

---

### 3. PostgreSQL UPSERT

중복 데이터 처리를 위해 PostgreSQL의 `ON CONFLICT` 구문을 적극 활용했습니다.

* Match

  * `ON CONFLICT DO NOTHING`
  * 이미 저장된 경기의 중복 저장 방지

* Player

  * `ON CONFLICT DO UPDATE`
  * 플레이어는 유지하면서 닉네임 등 변경 가능한 정보만 최신 상태로 갱신

* Character Statistics

  * Batch UPSERT를 통한 누적 통계 갱신

* Character Matchup

  * Batch UPSERT를 통한 상성 데이터 갱신

이를 통해 별도의 조회 후 저장하는 과정을 제거하고, 하나의 SQL로 데이터 무결성과 성능을 함께 확보했습니다.

---

### 4. Repository Optimization

일반적인 조회와 단건 CRUD는 Spring Data JPA를 사용했습니다.

반면 대량 INSERT 및 UPSERT가 필요한 구간은 JdbcClient를 사용하여 직접 SQL을 작성했습니다.

이를 통해 ORM이 생성하는 다수의 SQL 대신 하나의 Batch SQL을 실행하도록 최적화했습니다.

---

### Optimization Summary

| Optimization             | Effect                    |
| ------------------------ | ------------------------- |
| Batch INSERT             | INSERT 횟수 감소 및 네트워크 비용 절감 |
| Batch UPSERT             | 중복 조회 없이 데이터 갱신           |
| Aggregation              | 동일 데이터에 대한 반복 UPDATE 감소   |
| RabbitMQ                 | 데이터 수집과 저장을 비동기로 분리       |
| JdbcClient               | 대량 데이터 저장 성능 향상           |
| PostgreSQL `ON CONFLICT` | 데이터 무결성과 저장 효율 확보         |

이 프로젝트는 단순한 CRUD 애플리케이션이 아니라, **대량의 리플레이 데이터를 효율적으로 처리하기 위한 데이터 처리 파이프라인**을 구현하는 데 초점을 맞추었습니다. 성능 최적화는 단순히 SQL을 줄이는 수준을 넘어, 데이터 흐름과 저장 전략을 함께 설계하는 방향으로 진행했습니다.

---

## API Documentation

프로젝트는 **SpringDoc OpenAPI (Swagger)**를 적용하여 REST API 문서를 자동으로 제공합니다.

애플리케이션 실행 후 아래 주소에서 API 명세를 확인하고 직접 테스트할 수 있습니다.

### Swagger UI

```
http://localhost:8080/swagger-ui.html
```

또는

```
http://localhost:8080/swagger-ui/index.html
```

---

## Available APIs

### Player API

| Method | Endpoint                    | Description   |
| ------ | --------------------------- | ------------- |
| GET    | `/players/{userId}`         | 플레이어 요약 정보 조회 |
| GET    | `/players/{userId}/matches` | 플레이어 최근 경기 조회 |

---

### Character Statistics API

| Method | Endpoint              | Description     |
| ------ | --------------------- | --------------- |
| GET    | `/characters/stats`   | 캐릭터별 승률 및 전적 조회 |
| GET    | `/characters/ranking` | 캐릭터 랭킹 조회       |

---

### Character Matchup API

| Method | Endpoint                           | Description      |
| ------ | ---------------------------------- | ---------------- |
| GET    | `/characters/{character}/matchups` | 특정 캐릭터의 상성 정보 조회 |

---

## Pagination

최근 경기 조회 API는 Spring Pageable을 사용한 페이지네이션을 지원합니다.

### Example

```
GET /players/76561198000000000/matches?page=0&size=20
```

---

## Filtering & Sorting

캐릭터 상성 API는 최소 경기 수와 정렬 기준을 지정할 수 있습니다.

### Example

```
GET /characters/JIN/matchups?minMatches=100&sort=WIN_RATE
```

### Query Parameters

| Parameter    | Description                                     |
| ------------ | ----------------------------------------------- |
| `minMatches` | 최소 경기 수 이상인 데이터만 조회                             |
| `sort`       | 정렬 기준 (`MATCHES`, `WIN_RATE`, `WINS`, `LOSSES`) |

---

## Response Format

페이지 조회 API는 공통 `PageResponse<T>` 형식을 사용합니다.

```json
{
  "content": [
    {
      "battleId": "...",
      "battleAt": "...",
      "character": "Jin",
      "opponentCharacter": "Kazuya",
      "opponentNickname": "Player",
      "winner": true
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 120,
  "totalPages": 6,
  "last": false
}
```

---

## Error Response

예외 발생 시 공통 응답 형식을 사용합니다.

```json
{
  "code": "PLAYER_NOT_FOUND",
  "message": "Player not found."
}
```

Swagger UI에서는 각 API의 요청 파라미터, 응답 모델, HTTP 상태 코드 및 예시를 함께 확인할 수 있습니다.

--- 

## Getting Started

### Prerequisites

프로젝트를 실행하기 위해 다음 환경이 필요합니다.

| Software       | Version    |
| -------------- | ---------- |
| Java           | 21         |
| Docker         | Latest     |
| Docker Compose | Latest     |
| Gradle         | Wrapper 사용 |

---

## 1. Clone Repository

```bash
git clone https://github.com/<your-github-id>/tekken-analytics.git
cd tekken-analytics
```

---

## 2. Start Infrastructure

Docker Compose를 이용하여 PostgreSQL과 RabbitMQ를 실행합니다.

```bash
docker compose up -d
```

실행되는 서비스는 다음과 같습니다.

| Service             | Port  |
| ------------------- | ----- |
| PostgreSQL          | 5432  |
| RabbitMQ            | 5672  |
| RabbitMQ Management | 15672 |

RabbitMQ Management UI

```text
http://localhost:15672
```

기본 계정

```text
Username : guest
Password : guest
```

---

## 3. Run Application

Gradle Wrapper를 이용하여 애플리케이션을 실행합니다.

```bash
./gradlew bootRun
```

또는

```bash
./gradlew build
java -jar build/libs/*.jar
```

---

## 4. Access Swagger UI

애플리케이션 실행 후 Swagger UI에서 API를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui/index.html
```

---

## 5. Verify Services

정상적으로 실행되면 다음 서비스를 사용할 수 있습니다.

| Service             | URL                                         |
| ------------------- | ------------------------------------------- |
| Application         | http://localhost:8080                       |
| Swagger UI          | http://localhost:8080/swagger-ui/index.html |
| RabbitMQ Management | http://localhost:15672                      |

---

## Running Tests

### Unit Tests

```bash
./gradlew test
```

---

### Integration Tests

Repository 통합 테스트는 Testcontainers를 사용하여 실제 PostgreSQL 컨테이너에서 실행됩니다.

Docker가 실행 중인 상태에서 아래 명령으로 실행할 수 있습니다.

```bash
./gradlew test
```

---

## Project Configuration

기본 개발 환경은 Docker Compose 기반으로 구성되어 있습니다.

* PostgreSQL
* RabbitMQ
* Spring Boot

애플리케이션은 실행 시 PostgreSQL에 연결하고, RabbitMQ를 통해 리플레이 데이터를 비동기적으로 처리합니다.

---

## Development Workflow

프로젝트 실행 순서는 다음과 같습니다.

```text
1. docker compose up -d

        ↓

2. ./gradlew bootRun

        ↓

3. Open Swagger UI

        ↓

4. Execute APIs

        ↓

5. Verify PostgreSQL & RabbitMQ
```

이 과정을 통해 로컬 환경에서 데이터 수집, 비동기 처리, 통계 집계, 조회 API까지 전체 기능을 확인할 수 있습니다.

---

## Testing

프로젝트의 핵심 비즈니스 로직과 데이터 저장 로직을 검증하기 위해 **단위 테스트(Unit Test)**와 **통합 테스트(Integration Test)**를 작성했습니다.

단위 테스트는 서비스 및 집계(Aggregator) 로직을 검증하고, 통합 테스트는 실제 PostgreSQL 환경에서 Repository의 SQL 동작을 검증합니다.

---

## Test Strategy

| Test Type        | Purpose                           |
| ---------------- | --------------------------------- |
| Unit Test        | 서비스 및 비즈니스 로직 검증                  |
| Integration Test | 실제 PostgreSQL에서 Repository SQL 검증 |

---

## Unit Tests

### PlayerQueryService

* 플레이어 요약 정보 조회
* 최근 경기 조회
* 존재하지 않는 플레이어 예외 처리

---

### CharacterStatsService

* 캐릭터 통계 조회
* 캐릭터 랭킹 계산
* 승률 계산 검증

---

### CharacterMatchupService

* 캐릭터 상성 조회
* 최소 경기 수 필터링
* 다양한 정렬 조건 검증

---

### CharacterStatsAggregator

* 통계 누적
* Flush 동작
* Batch Update 호출 검증

---

### CharacterMatchupAggregator

* 상성 통계 누적
* Flush 동작
* Matchup 집계 검증

---

## Repository Integration Tests

Repository는 **Testcontainers + PostgreSQL** 환경에서 테스트하여 운영 환경과 동일한 SQL 동작을 검증했습니다.

### PlayerRepository

검증 항목

* Player 신규 저장
* Player UPSERT
* Batch UPSERT
* 닉네임 변경(Update)
* 반환 ID 검증

---

### MatchRepository

검증 항목

* Match 신규 저장
* 중복 Match 저장 방지
* Batch INSERT
* `insertIfAbsentAll()` 반환값 검증
* 빈 입력 처리

---

### MatchParticipantRepository

검증 항목

* Participant 저장
* Batch INSERT
* 다건 저장
* 빈 입력 처리

---

## Why Testcontainers?

Repository는 PostgreSQL 전용 기능을 적극 활용합니다.

* `ON CONFLICT`
* `RETURNING`
* Batch INSERT
* Batch UPSERT

이러한 기능은 운영 데이터베이스와 동일한 환경에서 검증하는 것이 중요하므로 Testcontainers를 사용했습니다.

---

## Test Execution

모든 테스트는 다음 명령으로 실행할 수 있습니다.

```bash
./gradlew test
```

Repository 통합 테스트는 Docker를 이용하여 PostgreSQL 컨테이너를 자동으로 생성한 뒤 테스트를 수행합니다.

---

## Test Coverage

현재 테스트는 다음 영역을 포함합니다.

* 서비스 비즈니스 로직
* Aggregator 동작
* PostgreSQL UPSERT
* Batch INSERT
* Batch UPSERT
* Repository SQL
* 예외 처리
* 데이터 무결성

핵심 데이터 처리 로직과 데이터베이스 저장 전략을 중심으로 테스트를 작성하여, 서비스 로직뿐 아니라 실제 SQL이 의도한 대로 동작하는지도 함께 검증했습니다.
