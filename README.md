# Common Spring Boot Starter

A Spring Boot 3.x Starter that provides auto-configuration for Snowflake ID generator.

## Installation

### Step 1: Build and Install Locally

Since this is a SNAPSHOT version, you need to install it to your local Maven repository first:
```bash
git clone <repository-url>
cd common-spring-boot-starter
./gradlew clean publishToMavenLocal
```

### Step 2: Configure Repository

Add `mavenLocal()` to your project's repository configuration.

**Option A: settings.gradle (Recommended)**
```gradle
dependencyResolutionManagement {
    repositories {
        mavenLocal()  // Required for local SNAPSHOT
        mavenCentral()
    }
}
```

**Option B: build.gradle**
```gradle
repositories {
    mavenLocal()
    mavenCentral()
}
```

### Step 3: Add Dependency
```gradle
dependencies {
    implementation 'com.kazama:common-spring-boot-starter:1.0.0-SNAPSHOT'
}
```

> **Note:** All team members must run `./gradlew publishToMavenLocal` in the starter project before using it.

## Usage

### Snowflake ID Generator

The Snowflake ID generator provides distributed unique ID generation with timestamp, datacenter, and worker machine information encoded in a 64-bit Long value.

#### Configuration (application.yml)
```yaml
common:
  snowflake:
    worker-id: 1        # Optional, defaults to auto-generation from IP
    datacenter-id: 1    # Optional, defaults to 1
```

Alternatively, use environment variables:
```properties
WORKER_ID=1
DATACENTER_ID=1
```

#### Auto-generate ID for JPA Entities
```java
import jakarta.persistence.*;
import com.kazama.common.snowflake.SnowflakeId;

@Entity
public class User {
    @Id
    @SnowflakeId  // Auto-generated on save
    private Long id;

    private String username;
    private String email;
    
    // getters and setters...
}
```

#### Manual ID Generation
```java
import com.kazama.common.snowflake.SnowflakeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    
    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    public void createOrder() {
        long orderId = snowflakeGenerator.nextId();
        System.out.println("Generated Order ID: " + orderId);
        // Use the ID for your business logic
    }
}
```

## Complete Example
```java
import jakarta.persistence.*;
import com.kazama.common.snowflake.SnowflakeId;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @SnowflakeId
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // getters and setters...
}

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
    
    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    public Product createProduct(String name, Double price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setCreatedAt(LocalDateTime.now());
        
        // ID will be auto-generated via @SnowflakeId annotation
        return repository.save(product);
    }
    
    public Long generateCustomId() {
        // Manual ID generation when needed
        return snowflakeGenerator.nextId();
    }
}
```

## How Snowflake ID Works

Snowflake ID is a 64-bit distributed ID generation algorithm:
```
| 1 bit (unused) | 41 bits (timestamp) | 5 bits (datacenter) | 5 bits (worker) | 12 bits (sequence) |
```

- **Timestamp (41 bits)**: Milliseconds since custom epoch
- **Datacenter ID (5 bits)**: Supports up to 32 datacenters
- **Worker ID (5 bits)**: Supports up to 32 workers per datacenter
- **Sequence (12 bits)**: 4096 IDs per millisecond per worker

**Benefits:**
- ✅ Globally unique across distributed systems
- ✅ Roughly time-ordered (sortable)
- ✅ No database coordination required
- ✅ High performance (millions of IDs/second)

## Requirements

- Java 21+
- Spring Boot 3.5.10+
- Spring Data JPA (for `@SnowflakeId` annotation)

## Troubleshooting

### "Could not find com.kazama:common-spring-boot-starter:1.0.0-SNAPSHOT"

**Cause:** The starter is not installed in your local Maven repository.

**Solution:**
```bash
cd common-spring-boot-starter
./gradlew publishToMavenLocal
```

Verify installation:
```bash
ls ~/.m2/repository/com/kazama/common-spring-boot-starter/1.0.0-SNAPSHOT/
```

### Changes Not Reflecting After Update

SNAPSHOT versions are cached by Gradle. Force refresh:
```bash
./gradlew clean build --refresh-dependencies
```

Or delete Gradle cache:
```bash
rm -rf ~/.gradle/caches/modules-2/files-2.1/com.kazama/
```

### Worker ID Conflicts in Development

If running multiple instances locally, manually assign unique worker IDs:
```yaml
# Instance 1
common:
  snowflake:
    worker-id: 1

# Instance 2
common:
  snowflake:
    worker-id: 2
```

## Roadmap

- [ ] Redisson Redis client auto-configuration
- [ ] Distributed tracing support
- [ ] More ID generation strategies (UUID, ULID)
- [ ] Monitoring and metrics integration

## License

MIT License