# Open8 Java 27

A custom Java distribution with 6 new language features: **JSON literals**, **auto-return**, **structural copy**, **pattern match**, **null safety**, and **async/await**.

```
open8 version "27.0.1"
Open8 Java Runtime Environment
Open8 Java 27 64-Bit Server VM
```

## Quick Start

```bash
tar -xzf open8-java-27.0.1_linux-aarch64.tar.gz
export JAVA_HOME=$(pwd)/jdk
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```

## Features at a Glance

```java
import java.json.Json;

// JSON literals
Json config = {"host": "localhost", "port": 8080};

// Null safety
String host = config?.host?.asString() ?? "0.0.0.0";

// Pattern matching
String desc = match (shape) {
    case Circle(double r)    -> "circle";
    case Rectangle(double w, double h) -> "rect";
};

// Async/await
async String fetch(String url) throws Exception {
    return client.send(request, BodyHandlers.ofString()).body();
}
String page = await fetch("https://example.com");

// Auto-return (no explicit return needed)
int defaultZero() { }  // returns 0

// Structural copy
target = copy source;  // copies matching fields
```

## Building from Source

```bash
git clone https://github.com/open8/java.git
cd java
bash configure --with-boot-jdk=/path/to/jdk25 --with-debug-level=release
make images
./build/*/images/jdk/bin/java -version
```

## License

GPL v2 + Classpath Exception (same as OpenJDK)
