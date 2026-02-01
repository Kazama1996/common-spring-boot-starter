# Common Spring Boot Starter

A Spring Boot 3.x Starter that provides auto-configuration for Snowflake ID generator and Redisson Redis client.

## Installation

```gradle
dependencies {
    implementation 'com.kazama:common-spring-boot-starter:1.0.0-SNAPSHOT'
}
```

## Usage

### 1. Snowflake ID Generator

#### Configuration (application.yml)

```yaml
common:
  snowflake:
    worker-id: 1        # Optional, defaults to auto-generation from IP
    datacenter-id: 1    # Optional, defaults to 1
```

Alternatively, use environment variables: `WORKER_ID`, `DATACENTER_ID`

#### Auto-generate ID for JPA Entities

```java
import jakarta.persistence.*;
import org.kazama.snowflake.SnowflakeId;

@Entity
public class User {
    @Id
    @SnowflakeId  // Auto-generated on save
    private Long id;

    private String username;
}
```

#### Manual ID Generation

```java
@Autowired
private SnowflakeGenerator snowflakeGenerator;

public void createOrder() {
    long id = snowflakeGenerator.nextId();
}
```

### 2. Redisson Redis Client

#### Configuration (application.yml)

```yaml
common:
  redisson:
    address: redis://127.0.0.1:6379
    password: your_password      # Optional
    database: 0
    connection-pool-size: 64
    connection-minimum-idle-size: 10
```

#### Usage Examples

```java
@Autowired
private RedissonClient redissonClient;

// Cache operations
public void cache() {
    RBucket<String> bucket = redissonClient.getBucket("key");
    bucket.set("value", 1, TimeUnit.HOURS);
    String value = bucket.get();
}

// Distributed lock
public void lock() {
    RLock lock = redissonClient.getLock("myLock");
    try {
        if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
            try {
                // Business logic
            } finally {
                lock.unlock();
            }
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}

// Map/List/Set
public void collections() {
    RMap<String, String> map = redissonClient.getMap("myMap");
    map.put("key", "value");

    RList<String> list = redissonClient.getList("myList");
    list.add("item");

    RSet<String> set = redissonClient.getSet("mySet");
    set.add("element");
}

// Rate limiter
public void rateLimiter() {
    RRateLimiter limiter = redissonClient.getRateLimiter("myLimiter");
    limiter.trySetRate(RateType.OVERALL, 10, 5, RateIntervalUnit.SECONDS);

    if (limiter.tryAcquire()) {
        // Allow request
    }
}
```

## Complete Example

```java
@Entity
class Product {
    @Id
    @SnowflakeId
    private Long id;
    private String name;
    private Double price;
}

@Service
class ProductService {
    @Autowired
    private ProductRepository repository;

    @Autowired
    private RedissonClient redissonClient;

    public Product getWithCache(Long id) {
        String key = "product:" + id;

        // Try to get from cache
        Product cached = redissonClient.<Product>getBucket(key).get();
        if (cached != null) return cached;

        // Query from database and cache
        Product product = repository.findById(id).orElse(null);
        if (product != null) {
            redissonClient.getBucket(key).set(product);
        }
        return product;
    }
}
```

## Requirements

- Java 21+
- Spring Boot 3.5.10+
