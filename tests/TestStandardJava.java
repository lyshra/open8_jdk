/**
 * Regression test: standard Java features must still work.
 */
import java.util.List;
import java.util.Map;

public class TestStandardJava {

    // Record
    record Point(int x, int y) {}

    // Sealed interface
    sealed interface Animal permits Dog, Cat {}
    record Dog(String name) implements Animal {}
    record Cat(String name) implements Animal {}

    public static void main(String[] args) {
        // Array initializer (must NOT be confused with JSON)
        int[] arr = {1, 2, 3};
        assert arr[0] == 1 : "array initializer should work";

        // Block statement
        if (true) {
            System.out.println("block statement works");
        }

        // Lambda
        Runnable r = () -> { System.out.println("lambda works"); };
        r.run();

        // Switch expression
        int x = 2;
        String s = switch (x) {
            case 1 -> "one";
            case 2 -> "two";
            default -> "other";
        };
        assert s.equals("two") : "switch expression should work";

        // Pattern matching instanceof
        Object obj = "hello";
        if (obj instanceof String str) {
            assert str.equals("hello") : "pattern matching instanceof should work";
        }

        // Records
        Point p = new Point(3, 4);
        assert p.x() == 3 && p.y() == 4 : "records should work";

        // Sealed classes with switch
        Animal a = new Dog("Rex");
        String desc = switch (a) {
            case Dog d -> "Dog: " + d.name();
            case Cat c -> "Cat: " + c.name();
        };
        assert desc.equals("Dog: Rex") : "sealed switch should work";

        // Text blocks
        String text = """
                Hello
                World""";
        assert text.contains("Hello") : "text blocks should work";

        // var
        var list = List.of(1, 2, 3);
        assert list.size() == 3 : "var should work";

        System.out.println("All standard Java regression tests passed!");
    }
}
