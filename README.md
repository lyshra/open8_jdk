<p align="center">
  <h1 align="center">Open8 JDK 27</h1>
  <p align="center">
    <strong>A modern Java distribution with the features you've been waiting for.</strong>
  </p>
  <p align="center">
    <a href="#installation">Installation</a> &middot;
    <a href="#features">Features</a> &middot;
    <a href="#getting-started">Getting Started</a> &middot;
    <a href="#building-from-source">Build from Source</a>
  </p>
</p>

---

**Open8 JDK 27** is a custom Java distribution that extends OpenJDK with **6 new language features** inspired by TypeScript, Kotlin, and C#. It's a **drop-in replacement** for your existing JDK — all your current Java code compiles and runs unchanged, and you get powerful new syntax on top.

```
$ java -version
open8 version "27.0.1" 2026-03-27
Open8 Java Runtime Environment (build 27.0.1)
Open8 Java 27 64-Bit Server VM (build 27.0.1, mixed mode, sharing)
```

### Why Open8 JDK 27?

| What you write today (standard Java) | What you write with Open8 JDK 27 |
|---|---|
| `if (user != null && user.address != null) { city = user.address.city; }` | `city = user?.address?.city;` |
| `JSONObject obj = new JSONObject(); obj.put("name", "Alice");` | `Json obj = {"name": "Alice"};` |
| `CompletableFuture.supplyAsync(() -> fetchData())` | `async String fetchData() { ... }` |
| `switch(shape) { case Circle c -> ... }` | `match(shape) { case Circle(double r) -> ... }` |
| Compile error: "missing return statement" | Auto-returns `null`, `0`, `false` |
| Manual field-by-field copying | `target = copy source;` |

---

## Installation

### Download and Extract

```bash
# Download the release
curl -LO https://github.com/lyshra/open8_jdk/releases/download/v27.0.1/open8-java-27.0.1_linux-aarch64.tar.gz

# Extract
tar -xzf open8-java-27.0.1_linux-aarch64.tar.gz

# Set as your JDK
export JAVA_HOME=$(pwd)/jdk
export PATH="$JAVA_HOME/bin:$PATH"

# Verify
java -version
```

### Make It Permanent

Add to your `~/.bashrc` or `~/.zshrc`:

```bash
export JAVA_HOME=/path/to/open8-jdk
export PATH="$JAVA_HOME/bin:$PATH"
```

### SDKMAN (coming soon)

```bash
sdk install java 27.0.1-open8
```

---

## Features

### 1. Native JSON Support

No more third-party libraries for simple JSON. Open8 JDK 27 adds a built-in `Json` type with literal syntax, dot access, bracket access, and mutation — all at the language level.

```java
import java.json.Json;

// Create JSON with literal syntax — just like JavaScript
Json person = {"name": "Alice", "age": 30, "active": true};
Json scores = [100, 95, 88];
Json nested = {"user": {"name": "Bob", "scores": [100, 95, 88]}};

// Read values with dot notation
String name = person.name.asString();       // "Alice"
int age = person.age.asInt();               // 30

// Bracket access works too
String also = person["name"].asString();    // "Alice"

// Mutate in place
person.city = Json.of("NYC");
person["population"] = Json.of(8_000_000);

// Empty literals
Json emptyObj = {};
Json emptyArr = [];
```

**Before (standard Java with Jackson/Gson):**
```java
ObjectMapper mapper = new ObjectMapper();
ObjectNode person = mapper.createObjectNode();
person.put("name", "Alice");
person.put("age", 30);
person.put("active", true);
String name = person.get("name").asText();
```

**After (Open8 JDK 27):**
```java
Json person = {"name": "Alice", "age": 30, "active": true};
String name = person.name.asString();
```

---

### 2. Null Safety Operators (`?.` and `??`)

The #1 source of Java bugs — `NullPointerException` — is now easy to prevent with optional chaining and null coalescing operators, just like TypeScript and Kotlin.

```java
// Optional chaining — short-circuits to null if any part is null
String city = user?.address?.city;

// Null coalescing — provide a default when null
String name = user?.name ?? "Anonymous";

// Chain them together
String display = order?.customer?.email ?? "no-email@example.com";

// Works with any depth
String zip = company?.headquarters?.address?.zipCode ?? "00000";
```

**Before (standard Java):**
```java
String city = null;
if (user != null && user.address != null) {
    city = user.address.city;
}
String name = user != null && user.name != null ? user.name : "Anonymous";
```

**After (Open8 JDK 27):**
```java
String city = user?.address?.city;
String name = user?.name ?? "Anonymous";
```

---

### 3. Exhaustive Pattern Matching (`match`)

Kotlin-style `match` expressions that work with sealed types, records, and any value. The compiler ensures every case is covered.

```java
sealed interface Shape permits Circle, Rectangle {}
record Circle(double radius) implements Shape {}
record Rectangle(double w, double h) implements Shape {}

// Destructure records directly in the match
String describe(Shape shape) {
    return match (shape) {
        case Circle(double r)              -> "Circle with radius " + r;
        case Rectangle(double w, double h) -> "Rectangle " + w + " x " + h;
    };
}

// Match on values with an else clause
String httpStatus(int code) {
    return match (code) {
        case 200 -> "OK";
        case 404 -> "Not Found";
        case 500 -> "Internal Server Error";
        default  -> "Unknown: " + code;
    };
}
```

**Before (standard Java):**
```java
String desc = switch (shape) {
    case Circle c      -> "Circle with radius " + c.radius();
    case Rectangle r   -> "Rectangle " + r.w() + " x " + r.h();
};
```

**After (Open8 JDK 27):**
```java
String desc = match (shape) {
    case Circle(double r)              -> "Circle with radius " + r;
    case Rectangle(double w, double h) -> "Rectangle " + w + " x " + h;
};
```

Record components are destructured inline — no need to call accessor methods.

---

### 4. Async/Await

Write asynchronous code that reads like synchronous code. Mark methods `async` and use `await` to get results from `Future<T>` values.

```java
import java.util.concurrent.*;

// Declare an async method — returns Future<String> to callers
async String fetchData(String url) throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
    return client.send(request, BodyHandlers.ofString()).body();
}

// Use await to get the result
String page = await fetchData("https://api.example.com/data");

// Or hold the Future and await later
Future<String> task = fetchData("https://api.example.com/slow");
// ... do other work ...
String result = await task;
```

**Before (standard Java):**
```java
CompletableFuture<String> fetchData(String url) {
    return CompletableFuture.supplyAsync(() -> {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).build();
            return client.send(request, BodyHandlers.ofString()).body();
        } catch (Exception e) { throw new RuntimeException(e); }
    });
}
String page = fetchData("https://api.example.com/data").get();
```

**After (Open8 JDK 27):**
```java
async String fetchData(String url) throws Exception {
    return client.send(request, BodyHandlers.ofString()).body();
}
String page = await fetchData("https://api.example.com/data");
```

---

### 5. Auto-Return Defaults

No more "missing return statement" errors. If a method doesn't explicitly return on every path, Open8 JDK 27 automatically returns the type's zero value.

```java
// Returns null automatically
String getName() {
    System.out.println("called");
}

// Returns 0 on the else path
int compute(boolean flag) {
    if (flag) return 42;
}

// All primitive defaults work
boolean isReady() { }   // returns false
double getScore() { }   // returns 0.0
long getCount() { }     // returns 0L
```

| Return Type | Auto-Return Value |
|---|---|
| `boolean` | `false` |
| `byte`, `short`, `int` | `0` |
| `long` | `0L` |
| `float` | `0.0f` |
| `double` | `0.0` |
| `char` | `'\0'` |
| Any object | `null` |

---

### 6. Structural Field Copying (`copy`)

Copy all matching fields from one object to another by name and type — no manual getters/setters, no reflection, no mapping libraries.

```java
class UserDTO { String name; String email; int age; }
class UserEntity { String name; String email; int age; long id; String passwordHash; }

UserDTO dto = new UserDTO();
dto.name = "Alice"; dto.email = "alice@example.com"; dto.age = 30;

UserEntity entity = new UserEntity();
entity.id = 1L;
entity.passwordHash = "abc123";

entity = copy dto;
// entity.name = "Alice", entity.email = "alice@example.com", entity.age = 30
// entity.id and entity.passwordHash are UNCHANGED (no matching field in dto)
```

**Before (standard Java):**
```java
entity.setName(dto.getName());
entity.setEmail(dto.getEmail());
entity.setAge(dto.getAge());
// Or use ModelMapper/MapStruct/BeanUtils...
```

**After (Open8 JDK 27):**
```java
entity = copy dto;
```

---

## Getting Started

### Using Open8 JDK 27 in a Project

Open8 JDK 27 is a standard JDK — it works with all your existing tools.

#### With any text editor + command line

```bash
# Write your code using the new features
cat > App.java << 'EOF'
import java.json.Json;

public class App {
    public static void main(String[] args) {
        Json config = {"app": "MyApp", "version": 1, "debug": false};
        String app = config.app.asString();
        boolean debug = config?.debug?.asBoolean() ?? true;
        System.out.println(app + " (debug=" + debug + ")");
    }
}
EOF

# Compile and run
javac App.java
java -cp . App
# Output: MyApp (debug=false)
```

#### With Maven

Set `JAVA_HOME` to Open8 JDK 27 before running Maven:

```bash
export JAVA_HOME=/path/to/open8-jdk
mvn clean compile
```

Or configure in `pom.xml`:

```xml
<properties>
    <maven.compiler.source>25</maven.compiler.source>
    <maven.compiler.target>25</maven.compiler.target>
    <maven.compiler.executable>${env.JAVA_HOME}/bin/javac</maven.compiler.executable>
</properties>
```

#### With Gradle

In `build.gradle`:

```groovy
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
```

Then run with:

```bash
JAVA_HOME=/path/to/open8-jdk gradle build
```

#### With IntelliJ IDEA

1. Go to **File > Project Structure > SDKs**
2. Click **+** > **Add JDK...**
3. Navigate to your Open8 JDK 27 directory
4. Set it as the Project SDK
5. IntelliJ will use Open8's compiler — new syntax features will compile correctly

#### With VS Code

Add to `.vscode/settings.json`:

```json
{
    "java.configuration.runtimes": [
        {
            "name": "Open8-27",
            "path": "/path/to/open8-jdk",
            "default": true
        }
    ]
}
```

---

## Example: Full Application

Here's a complete program using multiple Open8 features together:

```java
import java.json.Json;
import java.util.concurrent.*;

public class UserService {

    record User(String name, String email) {}

    // Async data fetching
    async String fetchUserJson(int id) throws Exception {
        Thread.sleep(100); // simulate network
        return "{\"name\": \"User" + id + "\", \"email\": \"user" + id + "@example.com\"}";
    }

    // Auto-return: returns null if user not found
    User findUser(Json data) {
        if (data != null) {
            String name = data?.name?.asString() ?? "Unknown";
            String email = data?.email?.asString() ?? "no-reply@example.com";
            return new User(name, email);
        }
    }

    // Pattern matching on results
    String describeUser(Object obj) {
        return match (obj) {
            case User(String name, String email) -> name + " <" + email + ">";
            default -> "Unknown user";
        };
    }

    public static void main(String[] args) throws Exception {
        var service = new UserService();

        // Await async call
        String raw = await service.fetchUserJson(42);
        Json data = Json.parse(raw);

        // Null-safe access + auto-return
        User user = service.findUser(data);

        // Pattern match the result
        String desc = service.describeUser(user);
        System.out.println(desc);
        // Output: User42 <user42@example.com>
    }
}
```

---

## Compatibility

Open8 JDK 27 is **100% backward compatible** with standard Java. All existing code, libraries, and frameworks work unchanged:

- All Java 1.0 through Java 25 syntax is supported
- All standard library APIs are available
- `javax.*`, `jakarta.*`, Spring, Quarkus, Micronaut — all work
- Maven, Gradle, Ant — all work
- JUnit, TestNG, Mockito — all work

The 6 new features are purely additive. If you don't use them, your code behaves exactly as it would on any other JDK.

---

## What's Inside

| Component | Description |
|---|---|
| `patches/` | Single patch file against OpenJDK 25 — all 6 features |
| `src/java.json/` | Runtime source for the `java.json` module (`Json`, `JsonType`) |
| `tests/` | 7 test suites covering all features + backward compatibility |
| `BUILD.md` | Step-by-step instructions to build from source |
| `RELEASE_NOTES.md` | Detailed release notes |

## Building from Source

```bash
# Clone OpenJDK 25
git clone https://github.com/openjdk/jdk25u.git && cd jdk25u

# Apply the Open8 patch
git apply /path/to/patches/open8-java-27-all-features.patch

# Copy the JSON runtime module
cp -r /path/to/src/java.json/ src/

# Configure and build
bash configure --with-boot-jdk=/path/to/jdk25 --with-debug-level=release
make images

# Verify
./build/*/images/jdk/bin/java -version
```

See [BUILD.md](BUILD.md) for full details.

---

## Current Release

| | |
|---|---|
| **Version** | 27.0.1 |
| **Based on** | OpenJDK 25 (jdk-25-ga) |
| **Platform** | Linux aarch64 |
| **License** | GPL v2 + Classpath Exception |

---

<p align="center">
  <strong>Open8 JDK 27</strong> — Java, the way it should be.
  <br>
  <a href="https://github.com/lyshra/open8_jdk/releases">Download</a> &middot;
  <a href="https://github.com/lyshra/open8_jdk/issues">Report Issues</a>
</p>
