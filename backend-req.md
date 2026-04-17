# Role

Act as an Expert Java Spring Boot Architect.

# Goal

Build the Backend for a "Tier 2: Web2 Custom Data Oracle". The system periodically fetches the SUI/USD price from 3 centralized exchanges, verifies the data using a Median Filter, stores it in PostgreSQL (Supabase), and exposes a public API.

# Tech Stack

- Java 17+, Spring Boot 3.x
- Dependencies: Spring Web, Spring Data JPA, PostgreSQL Driver, Lombok, SpringDoc OpenAPI.
- Database: PostgreSQL (Assume Supabase environment, configure HikariCP for connection pooling).

# Core Requirements

1. **Database Entity (`OraclePriceLog`)**
   - Fields: `id` (Long/UUID), `pair` (String, default "SUI/USD"), `verifiedPrice` (Double), `sourcesJson` (String/JSONB to store node status), `algorithm` (String), `timestamp` (Instant).
2. **The Aggregator & Verifier (`@Scheduled`)**
   - Run every 30 seconds.
   - Fetch mock/real SUI/USD prices from 3 exchanges (e.g., Binance, OKX, Bybit). Use `CompletableFuture` for concurrent fetching.
   - **Algorithm:** Calculate the Median of the 3 prices.
   - **Anomaly Detection:** If any source's price deviates by more than 5% from the median, mark its status as "ANOMALY_EXCLUDED". Otherwise, mark as "HEALTHY".
   - Save the verified record to the database using the Repository.

3. **Public API Endpoint**
   - Create `@RestController` with `GET /api/v1/oracle/sui-price`
   - Add `@CrossOrigin` to allow frontend access.
   - **Strict JSON Contract:** The response MUST exactly match this structure:
     {
     "pair": "SUI/USD",
     "verified_price": 1.2500,
     "sources": [
     {"name": "Binance", "price": 1.2400, "status": "HEALTHY"},
     {"name": "OKX", "price": 1.2600, "status": "HEALTHY"},
     {"name": "Bybit", "price": 0.0500, "status": "ANOMALY_EXCLUDED"}
     ],
     "algorithm": "Median Filter",
     "timestamp": "2026-04-17T22:15:00Z"
     }

# Output

Provide the Java code organized by standard layers: Controller, Service, Repository, Entity, and DTOs. Include the `application.yml` configuration.
