/**
 * Test auto-return default values.
 * Methods without explicit return should auto-return null/0/false/0.0/0L.
 */
public class TestAutoReturn {

    public String getString() {
        System.out.println("no explicit return");
        // should auto-return null
    }

    public int getInt() {
        // should auto-return 0
    }

    public boolean getBool() {
        // should auto-return false
    }

    public double getDouble() {
        // should auto-return 0.0
    }

    public long getLong() {
        // should auto-return 0L
    }

    public float getFloat() {
        // should auto-return 0.0f
    }

    public int conditional(boolean flag) {
        if (flag) return 42;
        // falls through → should auto-return 0
    }

    public void doVoid() {
        // unchanged — void returns are already handled
    }

    public int explicit() {
        return 99;
        // explicit return should work as before
    }

    public static void main(String[] args) {
        TestAutoReturn t = new TestAutoReturn();

        assert t.getString() == null : "getString should return null";
        assert t.getInt() == 0 : "getInt should return 0";
        assert t.getBool() == false : "getBool should return false";
        assert t.getDouble() == 0.0 : "getDouble should return 0.0";
        assert t.getLong() == 0L : "getLong should return 0L";
        assert t.getFloat() == 0.0f : "getFloat should return 0.0f";
        assert t.conditional(true) == 42 : "conditional(true) should return 42";
        assert t.conditional(false) == 0 : "conditional(false) should return 0";
        assert t.explicit() == 99 : "explicit should return 99";

        System.out.println("All auto-return tests passed!");
    }
}
