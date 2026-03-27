# Building Open8 Java 27 from Source

## Prerequisites

- Git
- JDK 25 (boot JDK) — e.g. Amazon Corretto 25: `sdk install java 25.0.2-amzn`
- GCC/G++ 14+
- Standard build tools: make, autoconf

## Steps

```bash
# 1. Clone OpenJDK 25
git clone https://github.com/openjdk/jdk25u.git
cd jdk25u

# 2. Apply the Open8 patch
git apply /path/to/patches/open8-java-27-all-features.patch

# 3. Copy the java.json runtime module
cp -r /path/to/src/java.json/ src/

# 4. Configure
bash configure \
  --with-boot-jdk=/path/to/jdk25 \
  --with-debug-level=release \
  --with-native-debug-symbols=none \
  --with-jvm-variants=server

# 5. Build
make images

# 6. Verify
./build/*/images/jdk/bin/java -version
# open8 version "27.0.1" 2026-03-27
# Open8 Java Runtime Environment (build 27.0.1...)
# Open8 Java 27 64-Bit Server VM (build 27.0.1...)
```

## Running Tests

```bash
JAVAC=./build/*/images/jdk/bin/javac
JAVA=./build/*/images/jdk/bin/java

cd /path/to/tests
$JAVAC *.java
$JAVA -ea -cp . TestStandardJava
$JAVA -ea -cp . TestAutoReturn
$JAVA -ea -cp . TestJson
$JAVA -ea -cp . TestCopy
$JAVA -ea -cp . TestMatch
$JAVA -ea -cp . TestNullSafety
$JAVA -ea -cp . TestAsync
```

## Patch Details

The patch modifies 24 files in `src/jdk.compiler/` and 3 files in `make/conf/`.
It also requires the new `src/java.json/` module (included separately since it's new files, not a diff).

Total: ~2500 lines added across parser, attribution, flow analysis, desugaring, and code generation.
