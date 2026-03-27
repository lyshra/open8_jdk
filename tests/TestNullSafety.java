/**
 * Test null safety operators: ?. ?? ?[]
 */
public class TestNullSafety {

    static class Address {
        String city;
        Address(String city) { this.city = city; }
    }

    static class User {
        String name;
        Address address;
        User(String name, Address address) {
            this.name = name;
            this.address = address;
        }
    }

    public static void main(String[] args) {
        // Test 1: Null-safe member access on null
        User user = null;
        String city = user?.address?.city;
        assert city == null : "null-safe on null should return null";
        System.out.println("Test 1 passed: null-safe on null → null");

        // Test 2: Null-safe member access on non-null
        user = new User("Alice", new Address("NYC"));
        String name = user?.name;
        assert name.equals("Alice") : "null-safe on non-null should return value";
        System.out.println("Test 2 passed: null-safe on non-null → Alice");

        // Test 3: Null-safe chain with null in middle
        User noAddr = new User("Bob", null);
        String addrCity = noAddr?.address?.city;
        assert addrCity == null : "null-safe chain should short-circuit at null";
        System.out.println("Test 3 passed: null-safe chain short-circuits");

        // Test 4: Null coalescing
        String result = null ?? "default";
        assert result.equals("default") : "null coalescing should return default";
        System.out.println("Test 4 passed: null ?? default → default");

        // Test 5: Null coalescing with non-null
        String result2 = "hello" ?? "default";
        assert result2.equals("hello") : "null coalescing should return left if non-null";
        System.out.println("Test 5 passed: hello ?? default → hello");

        // Test 6: Combined ?. and ??
        String cityOrDefault = user?.address?.city ?? "Unknown";
        assert cityOrDefault.equals("NYC") : "combined should return NYC";
        System.out.println("Test 6 passed: combined ?. and ??");

        System.out.println("All null safety tests passed!");
    }
}
