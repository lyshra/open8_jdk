import java.util.concurrent.*;

/**
 * Test async/await with virtual threads.
 */
public class TestAsync {

    // Simple async method that returns a value
    async String fetchData(String input) throws Exception {
        // Simulate some work
        Thread.sleep(10);
        return "result:" + input;
    }

    async int computeSum(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) throws Exception {
        TestAsync test = new TestAsync();

        // Test 1: Basic await
        String data = await test.fetchData("hello");
        assert data.equals("result:hello") : "basic await should work";
        System.out.println("Test 1 passed: " + data);

        // Test 2: Async returns Future
        Future<String> future = test.fetchData("world");
        String data2 = await future;
        assert data2.equals("result:world") : "await future should work";
        System.out.println("Test 2 passed: " + data2);

        // Test 3: Async with int return
        int sum = await test.computeSum(3, 4);
        assert sum == 7 : "async int should work";
        System.out.println("Test 3 passed: sum = " + sum);

        System.out.println("All async/await tests passed!");
    }
}
