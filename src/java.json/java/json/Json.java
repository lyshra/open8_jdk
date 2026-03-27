package java.json;

import java.util.*;
import java.util.stream.*;

/**
 * Represents a JSON value. A {@code Json} instance can hold a JSON object,
 * array, string, number, boolean, or null.
 *
 * @since 25
 */
public final class Json implements Iterable<Json> {

    private final Object value;
    private final JsonType type;

    // ---- Private constructors ----

    private Json(Object value, JsonType type) {
        this.value = value;
        this.type = type;
    }

    // ---- Static factory methods ----

    /** Create a Json from a String */
    public static Json of(String s) {
        return s == null ? ofNull() : new Json(s, JsonType.STRING);
    }

    /** Create a Json from an int */
    public static Json of(int n) {
        return new Json(n, JsonType.NUMBER);
    }

    /** Create a Json from a long */
    public static Json of(long n) {
        return new Json(n, JsonType.NUMBER);
    }

    /** Create a Json from a double */
    public static Json of(double n) {
        return new Json(n, JsonType.NUMBER);
    }

    /** Create a Json from a boolean */
    public static Json of(boolean b) {
        return new Json(b, JsonType.BOOLEAN);
    }

    /** Create the JSON null value */
    public static Json ofNull() {
        return new Json(null, JsonType.NULL);
    }

    /** Auto-boxing factory: wraps any Object into Json */
    @SuppressWarnings("unchecked")
    public static Json of(Object obj) {
        if (obj == null) return ofNull();
        if (obj instanceof Json j) return j;
        if (obj instanceof String s) return of(s);
        if (obj instanceof Integer n) return of(n.intValue());
        if (obj instanceof Long n) return of(n.longValue());
        if (obj instanceof Double n) return of(n.doubleValue());
        if (obj instanceof Float n) return of(n.doubleValue());
        if (obj instanceof Boolean b) return of(b.booleanValue());
        if (obj instanceof Number n) return of(n.doubleValue());
        if (obj instanceof Map<?,?> m) {
            var map = new LinkedHashMap<String, Json>();
            for (var e : m.entrySet()) {
                map.put(String.valueOf(e.getKey()), of(e.getValue()));
            }
            return new Json(map, JsonType.OBJECT);
        }
        if (obj instanceof List<?> list) {
            var arr = new ArrayList<Json>(list.size());
            for (var item : list) {
                arr.add(of(item));
            }
            return new Json(arr, JsonType.ARRAY);
        }
        if (obj instanceof Object[] array) {
            var arr = new ArrayList<Json>(array.length);
            for (var item : array) {
                arr.add(of(item));
            }
            return new Json(arr, JsonType.ARRAY);
        }
        return of(obj.toString());
    }

    /**
     * Create a JSON object from key-value pairs.
     * Usage: Json.ofEntries("key1", val1, "key2", val2, ...)
     * Values are auto-wrapped via Json.of() if not already Json.
     */
    public static Json ofEntries(Object... kvPairs) {
        if (kvPairs.length % 2 != 0) {
            throw new IllegalArgumentException("ofEntries requires even number of arguments (key-value pairs)");
        }
        var map = new LinkedHashMap<String, Json>();
        for (int i = 0; i < kvPairs.length; i += 2) {
            String key = String.valueOf(kvPairs[i]);
            Object val = kvPairs[i + 1];
            map.put(key, val instanceof Json j ? j : of(val));
        }
        return new Json(map, JsonType.OBJECT);
    }

    /** Create an empty JSON object */
    public static Json ofObject() {
        return new Json(new LinkedHashMap<String, Json>(), JsonType.OBJECT);
    }

    /** Create an empty JSON array */
    public static Json ofArray() {
        return new Json(new ArrayList<Json>(), JsonType.ARRAY);
    }

    /** Create a JSON array from a list of Json values */
    public static Json ofArray(List<Json> elements) {
        return new Json(new ArrayList<>(elements), JsonType.ARRAY);
    }

    /** Create a JSON array from varargs Json values (used by compiler desugaring) */
    public static Json ofElements(Json... elements) {
        ArrayList<Json> list = new ArrayList<>(elements.length);
        for (Json j : elements) list.add(j);
        return new Json(list, JsonType.ARRAY);
    }

    // ---- Parse from string ----

    /** Parse a JSON string into a Json value */
    public static Json parse(String jsonString) {
        return new JsonParser(jsonString).parseValue();
    }

    // ---- Type query ----

    public JsonType type() { return type; }
    public boolean isObject() { return type == JsonType.OBJECT; }
    public boolean isArray() { return type == JsonType.ARRAY; }
    public boolean isString() { return type == JsonType.STRING; }
    public boolean isNumber() { return type == JsonType.NUMBER; }
    public boolean isBoolean() { return type == JsonType.BOOLEAN; }
    public boolean isNull() { return type == JsonType.NULL; }

    // ---- Object accessors ----

    /** Get a value by key */
    @SuppressWarnings("unchecked")
    public Json get(String key) {
        if (type != JsonType.OBJECT) return ofNull();
        Json val = ((Map<String, Json>) value).get(key);
        return val != null ? val : ofNull();
    }

    /** Put a value by key (Json) */
    @SuppressWarnings("unchecked")
    public Json put(String key, Json val) {
        if (type != JsonType.OBJECT) throw new UnsupportedOperationException("Not a JSON object");
        ((Map<String, Json>) value).put(key, val != null ? val : ofNull());
        return this;
    }

    /** Put a value by key (auto-boxing) */
    public Json put(String key, Object val) {
        return put(key, val instanceof Json j ? j : of(val));
    }

    /** Check if a key exists */
    @SuppressWarnings("unchecked")
    public boolean has(String key) {
        if (type != JsonType.OBJECT) return false;
        return ((Map<String, Json>) value).containsKey(key);
    }

    /** Remove a key */
    @SuppressWarnings("unchecked")
    public Json remove(String key) {
        if (type != JsonType.OBJECT) throw new UnsupportedOperationException("Not a JSON object");
        ((Map<String, Json>) value).remove(key);
        return this;
    }

    /** Get all keys */
    @SuppressWarnings("unchecked")
    public Set<String> keys() {
        if (type != JsonType.OBJECT) return Set.of();
        return Collections.unmodifiableSet(((Map<String, Json>) value).keySet());
    }

    /** Get number of entries (object) or elements (array) */
    @SuppressWarnings("unchecked")
    public int size() {
        return switch (type) {
            case OBJECT -> ((Map<String, Json>) value).size();
            case ARRAY -> ((List<Json>) value).size();
            default -> 0;
        };
    }

    // ---- Array accessors ----

    /** Get element by index */
    @SuppressWarnings("unchecked")
    public Json get(int index) {
        if (type != JsonType.ARRAY) return ofNull();
        List<Json> list = (List<Json>) value;
        if (index < 0 || index >= list.size()) return ofNull();
        return list.get(index);
    }

    /** Set element at index */
    @SuppressWarnings("unchecked")
    public Json set(int index, Json val) {
        if (type != JsonType.ARRAY) throw new UnsupportedOperationException("Not a JSON array");
        ((List<Json>) value).set(index, val != null ? val : ofNull());
        return this;
    }

    /** Add element to array */
    @SuppressWarnings("unchecked")
    public Json add(Json val) {
        if (type != JsonType.ARRAY) throw new UnsupportedOperationException("Not a JSON array");
        ((List<Json>) value).add(val != null ? val : ofNull());
        return this;
    }

    /** Add element to array (auto-boxing) */
    public Json add(Object val) {
        return add(val instanceof Json j ? j : of(val));
    }

    // ---- Type coercion ----

    /** Convert to String */
    public String asString() {
        return switch (type) {
            case STRING -> (String) value;
            case NUMBER, BOOLEAN -> String.valueOf(value);
            case NULL -> null;
            default -> toString();
        };
    }

    /** Convert to int */
    public int asInt() {
        return switch (type) {
            case NUMBER -> ((Number) value).intValue();
            case STRING -> Integer.parseInt((String) value);
            case BOOLEAN -> ((Boolean) value) ? 1 : 0;
            default -> 0;
        };
    }

    /** Convert to long */
    public long asLong() {
        return switch (type) {
            case NUMBER -> ((Number) value).longValue();
            case STRING -> Long.parseLong((String) value);
            case BOOLEAN -> ((Boolean) value) ? 1L : 0L;
            default -> 0L;
        };
    }

    /** Convert to double */
    public double asDouble() {
        return switch (type) {
            case NUMBER -> ((Number) value).doubleValue();
            case STRING -> Double.parseDouble((String) value);
            case BOOLEAN -> ((Boolean) value) ? 1.0 : 0.0;
            default -> 0.0;
        };
    }

    /** Convert to boolean */
    public boolean asBoolean() {
        return switch (type) {
            case BOOLEAN -> (Boolean) value;
            case STRING -> Boolean.parseBoolean((String) value);
            case NUMBER -> ((Number) value).doubleValue() != 0;
            case NULL -> false;
            default -> true;
        };
    }

    /** Convert to Map */
    @SuppressWarnings("unchecked")
    public Map<String, Json> asMap() {
        if (type != JsonType.OBJECT) return Map.of();
        return Collections.unmodifiableMap((Map<String, Json>) value);
    }

    /** Convert to List */
    @SuppressWarnings("unchecked")
    public List<Json> asList() {
        if (type != JsonType.ARRAY) return List.of();
        return Collections.unmodifiableList((List<Json>) value);
    }

    // ---- Iterable (for arrays) ----

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Json> iterator() {
        if (type == JsonType.ARRAY) {
            return ((List<Json>) value).iterator();
        }
        return Collections.emptyIterator();
    }

    // ---- Object overrides ----

    @Override
    @SuppressWarnings("unchecked")
    public String toString() {
        return switch (type) {
            case OBJECT -> {
                var sb = new StringBuilder("{");
                var map = (Map<String, Json>) value;
                boolean first = true;
                for (var entry : map.entrySet()) {
                    if (!first) sb.append(",");
                    first = false;
                    sb.append(escapeString(entry.getKey()));
                    sb.append(":");
                    sb.append(entry.getValue().toString());
                }
                sb.append("}");
                yield sb.toString();
            }
            case ARRAY -> {
                var sb = new StringBuilder("[");
                var list = (List<Json>) value;
                boolean first = true;
                for (var item : list) {
                    if (!first) sb.append(",");
                    first = false;
                    sb.append(item.toString());
                }
                sb.append("]");
                yield sb.toString();
            }
            case STRING -> escapeString((String) value);
            case NUMBER -> {
                if (value instanceof Double d) {
                    if (d == Math.floor(d) && !Double.isInfinite(d)) {
                        yield String.valueOf(d.longValue());
                    }
                }
                yield String.valueOf(value);
            }
            case BOOLEAN -> String.valueOf(value);
            case NULL -> "null";
        };
    }

    private static String escapeString(String s) {
        var sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Json other)) return false;
        if (type != other.type) return false;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    // ---- Internal JSON Parser ----

    private static class JsonParser {
        private final String input;
        private int pos;

        JsonParser(String input) {
            this.input = input;
            this.pos = 0;
        }

        Json parseValue() {
            skipWhitespace();
            if (pos >= input.length()) return Json.ofNull();
            char c = input.charAt(pos);
            return switch (c) {
                case '{' -> parseObject();
                case '[' -> parseArray();
                case '"' -> parseString();
                case 't', 'f' -> parseBoolean();
                case 'n' -> parseNull();
                default -> parseNumber();
            };
        }

        private Json parseObject() {
            pos++; // skip {
            skipWhitespace();
            var map = new LinkedHashMap<String, Json>();
            if (pos < input.length() && input.charAt(pos) == '}') {
                pos++;
                return new Json(map, JsonType.OBJECT);
            }
            while (pos < input.length()) {
                skipWhitespace();
                String key = parseRawString();
                skipWhitespace();
                expect(':');
                Json val = parseValue();
                map.put(key, val);
                skipWhitespace();
                if (pos < input.length() && input.charAt(pos) == ',') {
                    pos++;
                } else {
                    break;
                }
            }
            skipWhitespace();
            expect('}');
            return new Json(map, JsonType.OBJECT);
        }

        private Json parseArray() {
            pos++; // skip [
            skipWhitespace();
            var list = new ArrayList<Json>();
            if (pos < input.length() && input.charAt(pos) == ']') {
                pos++;
                return new Json(list, JsonType.ARRAY);
            }
            while (pos < input.length()) {
                list.add(parseValue());
                skipWhitespace();
                if (pos < input.length() && input.charAt(pos) == ',') {
                    pos++;
                } else {
                    break;
                }
            }
            skipWhitespace();
            expect(']');
            return new Json(list, JsonType.ARRAY);
        }

        private Json parseString() {
            return Json.of(parseRawString());
        }

        private String parseRawString() {
            expect('"');
            var sb = new StringBuilder();
            while (pos < input.length()) {
                char c = input.charAt(pos++);
                if (c == '"') return sb.toString();
                if (c == '\\') {
                    if (pos >= input.length()) break;
                    char esc = input.charAt(pos++);
                    switch (esc) {
                        case '"' -> sb.append('"');
                        case '\\' -> sb.append('\\');
                        case '/' -> sb.append('/');
                        case 'b' -> sb.append('\b');
                        case 'f' -> sb.append('\f');
                        case 'n' -> sb.append('\n');
                        case 'r' -> sb.append('\r');
                        case 't' -> sb.append('\t');
                        case 'u' -> {
                            if (pos + 4 <= input.length()) {
                                sb.append((char) Integer.parseInt(input.substring(pos, pos + 4), 16));
                                pos += 4;
                            }
                        }
                        default -> sb.append(esc);
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        private Json parseNumber() {
            int start = pos;
            if (pos < input.length() && input.charAt(pos) == '-') pos++;
            while (pos < input.length() && Character.isDigit(input.charAt(pos))) pos++;
            boolean isFloat = false;
            if (pos < input.length() && input.charAt(pos) == '.') {
                isFloat = true;
                pos++;
                while (pos < input.length() && Character.isDigit(input.charAt(pos))) pos++;
            }
            if (pos < input.length() && (input.charAt(pos) == 'e' || input.charAt(pos) == 'E')) {
                isFloat = true;
                pos++;
                if (pos < input.length() && (input.charAt(pos) == '+' || input.charAt(pos) == '-')) pos++;
                while (pos < input.length() && Character.isDigit(input.charAt(pos))) pos++;
            }
            String numStr = input.substring(start, pos);
            if (isFloat) {
                return new Json(Double.parseDouble(numStr), JsonType.NUMBER);
            } else {
                long val = Long.parseLong(numStr);
                if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) {
                    return new Json((int) val, JsonType.NUMBER);
                }
                return new Json(val, JsonType.NUMBER);
            }
        }

        private Json parseBoolean() {
            if (input.startsWith("true", pos)) {
                pos += 4;
                return Json.of(true);
            } else if (input.startsWith("false", pos)) {
                pos += 5;
                return Json.of(false);
            }
            throw new IllegalArgumentException("Invalid JSON at position " + pos);
        }

        private Json parseNull() {
            if (input.startsWith("null", pos)) {
                pos += 4;
                return Json.ofNull();
            }
            throw new IllegalArgumentException("Invalid JSON at position " + pos);
        }

        private void skipWhitespace() {
            while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) pos++;
        }

        private void expect(char c) {
            if (pos >= input.length() || input.charAt(pos) != c) {
                throw new IllegalArgumentException("Expected '" + c + "' at position " + pos);
            }
            pos++;
        }
    }
}
