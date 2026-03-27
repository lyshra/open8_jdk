/**
 * Test match expression (exhaustive pattern matching).
 */
public class TestMatch {

    sealed interface Shape permits Circle, Rectangle {}
    record Circle(double r) implements Shape {}
    record Rectangle(double w, double h) implements Shape {}

    public static void main(String[] args) {
        // Test 1: Match with sealed interface — exhaustive
        Shape s = new Circle(5.0);
        String desc = match (s) {
            case Circle(double r)              -> "circle r=" + r;
            case Rectangle(double w, double h) -> "rect " + w + "x" + h;
        };
        assert desc.equals("circle r=5.0") : "match should return circle description";
        System.out.println("Test 1 passed: " + desc);

        // Test 2: Match with value
        int code = 200;
        String status = match (code) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            case 500 -> "Server Error";
            else     -> "Unknown: " + code;
        };
        assert status.equals("OK") : "match value should return OK";
        System.out.println("Test 2 passed: " + status);

        // Test 3: Match with else
        Shape s2 = new Rectangle(3.0, 4.0);
        String desc2 = match (s2) {
            case Circle(double r) -> "circle";
            else                  -> "not a circle";
        };
        assert desc2.equals("not a circle") : "match else should work";
        System.out.println("Test 3 passed: " + desc2);

        System.out.println("All match tests passed!");
    }
}
