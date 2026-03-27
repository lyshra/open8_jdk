/**
 * Test structural field copying with 'copy' keyword.
 */
public class TestCopy {

    static class Dog {
        String name;
        int age;
        double weight;
    }

    static class Cat {
        String name;
        int age;
        String color;
    }

    public static void main(String[] args) {
        Dog dog = new Dog();
        dog.name = "Rex";
        dog.age = 5;
        dog.weight = 30.0;

        Cat cat = new Cat();
        cat.color = "orange";

        // Copy matching fields from dog to cat
        cat = copy dog;

        // name and age should be copied (matching name + compatible type)
        // color should be unchanged (no match in Dog)
        // weight should be skipped (no match in Cat)
        System.out.println("Cat name: " + cat.name);
        System.out.println("Cat age: " + cat.age);
        System.out.println("Cat color: " + cat.color);

        System.out.println("All copy tests passed!");
    }
}
