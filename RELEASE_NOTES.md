# Open8 Java 27.0.1 Release Notes

## What is Open8 Java?

Open8 Java 27 is a custom Java distribution based on OpenJDK 25, extended with six new language features inspired by TypeScript and Kotlin. It is a drop-in replacement for any standard JDK — all existing Java code compiles and runs unchanged.

## New Language Features

### 1. Native JSON Support
Built-in `Json` type with literal syntax, dot/bracket access, and mutation.

```java
import java.json.Json;

Json person = {"name": "Alice", "age": 30};
Json scores = [100, 95, 88];

String name = person.name.asString();     // dot access
String also  = person["name"].asString(); // bracket access
person.city = Json.of("NYC");             // mutation
```

### 2. Auto-Return Defaults
Methods without an explicit return on every path automatically return the type's default value (`null`, `0`, `false`, `0.0`).

```java
String greet() {
    System.out.println("hello");
    // automatically returns null
}

int compute(boolean flag) {
    if (flag) return 42;
    // automatically returns 0
}
```

### 3. Structural Field Copying (`copy`)
Copy matching fields between any two classes by name and compatible type.

```java
class Dog { String name; int age; }
class Cat { String name; int age; String color; }

Dog d = new Dog(); d.name = "Rex"; d.age = 5;
Cat c = new Cat(); c.color = "orange";
c = copy d;  // c.name="Rex", c.age=5, c.color="orange" (unchanged)
```

### 4. Exhaustive Pattern Matching (`match`)
Kotlin/Scala-style `match` expression with exhaustiveness checking for sealed types.

```java
sealed interface Shape permits Circle, Rectangle {}
record Circle(double r) implements Shape {}
record Rectangle(double w, double h) implements Shape {}

String desc = match (shape) {
    case Circle(double r)            -> "circle r=" + r;
    case Rectangle(double w, double h) -> "rect " + w + "x" + h;
};
```

### 5. Null Safety (`?.` and `??`)
Optional chaining and null coalescing operators.

```java
String city = user?.address?.city;           // null if any is null
String name = user?.name ?? "Anonymous";     // null coalescing
```

### 6. Async/Await
Declare async methods that return `Future<T>`, and use `await` to unwrap them.

```java
async String fetchData(String url) throws Exception {
    return Http.get(url);
}

String data = await fetchData("https://api.example.com");
Future<String> task = fetchData("https://other.com");
String result = await task;
```

## System Requirements

- **OS**: Linux (aarch64)
- **Architecture**: ARM 64-bit (aarch64)
- **Minimum RAM**: 256 MB

## Installation

```bash
# Extract
tar -xzf open8-java-27.0.1_linux-aarch64.tar.gz

# Set JAVA_HOME
export JAVA_HOME=$(pwd)/jdk
export PATH="$JAVA_HOME/bin:$PATH"

# Verify
java -version
```

## Known Limitations

- **Null-safe method calls** (`obj?.method()`) are not yet supported — use `?.` for field access only
- **Async methods** execute synchronously (wrapped in `CompletableFuture.completedFuture`) — virtual thread parallelism is planned for a future release
- **Auto-return** suppresses "missing return statement" errors by design

## License

Open8 Java is distributed under the **GNU General Public License v2 with Classpath Exception**, the same license as OpenJDK.

## Based On

OpenJDK 25 (jdk-25-ga) — https://github.com/openjdk/jdk25u
