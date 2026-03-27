import java.json.Json;

/**
 * Test JSON literal support.
 * JSON objects and arrays created with literal syntax.
 */
public class TestJson {

    public static void main(String[] args) {
        // Test 1: JSON object literal
        Json person = {"name": "Alice", "age": 30};
        System.out.println("Person: " + person);
        assert person.get("name").asString().equals("Alice") : "name should be Alice";
        assert person.get("age").asInt() == 30 : "age should be 30";

        // Test 2: JSON array literal
        Json numbers = [1, 2, 3];
        System.out.println("Numbers: " + numbers);
        assert numbers.get(0).asInt() == 1 : "first element should be 1";

        // Test 3: Nested JSON
        Json nested = {"user": {"name": "Bob", "scores": [100, 95, 88]}};
        System.out.println("Nested: " + nested);

        // Test 4: Dot access (dynamic property)
        Json data = {"city": "NYC", "pop": 8000000};
        Json cityVal = data.city;
        System.out.println("City: " + cityVal);

        // Test 5: Bracket access
        Json val = data["city"];
        System.out.println("Bracket access: " + val);

        // Test 6: Mutation via dot
        data.city = Json.of("LA");
        System.out.println("After mutation: " + data);

        // Test 7: Mutation via bracket
        data["pop"] = Json.of(4000000);
        System.out.println("After bracket mutation: " + data);

        // Test 8: Empty object and array
        Json empty = Json.ofObject();
        Json emptyArr = Json.ofArray();
        System.out.println("Empty obj: " + empty);
        System.out.println("Empty arr: " + emptyArr);

        System.out.println("All JSON tests passed!");
    }
}
