package com.javahero.data;

import com.javahero.model.Answer;
import com.javahero.model.Hero;
import com.javahero.model.LearningPath;
import com.javahero.model.Question;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HeroDataLoader {

    public List<Hero> loadAllHeroes() {
        return List.of(
            buildVariableHero(),
            buildStringHero(),
            buildNumberHero(),
            buildBooleanHero(),
            buildIfHero(),
            buildLoopHero(),
            buildMethodHero(),
            buildClassHero(),
            buildObjectHero(),
            buildConstructorHero(),
            buildCollectionHero(),
            buildExceptionHero(),
            buildInterfaceHero(),
            buildGenericHero(),
            buildStreamHero(),
            buildLambdaHero(),
            buildOptionalHero(),
            buildRecordHero(),
            buildControllerHero(),
            buildServiceHero(),
            buildRepositoryHero(),
            buildBeanHero(),
            buildDependencyInjectionHero()
        );
    }

    private Answer a(String text) {
        return new Answer(text);
    }

    private Question q(String text, int correct, Answer... answers) {
        return new Question(text, List.of(answers), correct);
    }

    // ---- BRONZE ----

    private Hero buildVariableHero() {
        return new Hero("variable", "Variable Hero", LearningPath.BRONZE, 1,
            "Stores information for later use",
            "A variable is a named container that holds a value. You can change the value at any time. Variables are the building blocks of every program.",
            "String name = \"Alex\";\nint age = 25;",
            List.of(
                q("What is a variable in Java?",
                    1,
                    a("A method that returns a value"),
                    a("A named container that holds a value"),
                    a("A class without methods")),
                q("Which of these correctly declares an integer variable named 'score'?",
                    0,
                    a("int score = 10;"),
                    a("score int = 10;"),
                    a("integer score = 10;")),
                q("What happens to a variable's value when you assign a new value to it?",
                    2,
                    a("The old value stays and the new value is added"),
                    a("An error is thrown"),
                    a("The old value is replaced by the new one"))
            ));
    }

    private Hero buildStringHero() {
        return new Hero("string", "String Hero", LearningPath.BRONZE, 2,
            "Text that talks",
            "A String is a sequence of characters. Use it to store names, messages, and any text. Strings are one of the most used types in Java.",
            "String greeting = \"Hello, World!\";\nSystem.out.println(greeting);",
            List.of(
                q("What does a String represent in Java?",
                    0,
                    a("A sequence of characters"),
                    a("A whole number"),
                    a("A true/false value")),
                q("How do you create a String literal in Java?",
                    1,
                    a("Using single quotes: 'Hello'"),
                    a("Using double quotes: \"Hello\""),
                    a("Using backticks: `Hello`")),
                q("Which method prints a String to the console?",
                    2,
                    a("print(greeting)"),
                    a("console.log(greeting)"),
                    a("System.out.println(greeting)"))
            ));
    }

    private Hero buildNumberHero() {
        return new Hero("number", "Number Hero", LearningPath.BRONZE, 3,
            "Counts and calculates",
            "Java has int for whole numbers and double for decimals. Use numbers to count, calculate, and measure.",
            "int score = 42;\ndouble price = 9.99;",
            List.of(
                q("Which type stores whole numbers in Java?",
                    0,
                    a("int"),
                    a("double"),
                    a("String")),
                q("What is the correct type for storing a price like 9.99?",
                    1,
                    a("int"),
                    a("double"),
                    a("boolean")),
                q("What is the result of int x = 7 / 2 in Java?",
                    2,
                    a("3.5"),
                    a("4"),
                    a("3"))
            ));
    }

    private Hero buildBooleanHero() {
        return new Hero("boolean", "Boolean Hero", LearningPath.BRONZE, 4,
            "True or false, nothing more",
            "A boolean holds either true or false. Booleans power every decision in your code.",
            "boolean isReady = true;\nboolean isGameOver = false;",
            List.of(
                q("How many possible values does a boolean have?",
                    1,
                    a("Three: true, false, null"),
                    a("Two: true or false"),
                    a("One: only true")),
                q("Which of these is a valid boolean declaration?",
                    0,
                    a("boolean isReady = true;"),
                    a("bool isReady = true;"),
                    a("Boolean isReady = yes;")),
                q("Where are booleans commonly used?",
                    2,
                    a("Storing large text values"),
                    a("Performing arithmetic operations"),
                    a("Controlling if-statements and loops"))
            ));
    }

    private Hero buildIfHero() {
        return new Hero("if", "If Hero", LearningPath.BRONZE, 5,
            "Decides what to do next",
            "An if statement runs code only when a condition is true. Add else to handle the other case.",
            "if (score > 10) {\n    System.out.println(\"You win!\");\n} else {\n    System.out.println(\"Try again.\");\n}",
            List.of(
                q("What does an if statement do?",
                    1,
                    a("Repeats code a fixed number of times"),
                    a("Runs code only when a condition is true"),
                    a("Defines a reusable block of code")),
                q("What keyword handles the alternative case in an if statement?",
                    2,
                    a("otherwise"),
                    a("catch"),
                    a("else")),
                q("What type must the condition in an if statement evaluate to?",
                    0,
                    a("boolean"),
                    a("int"),
                    a("String"))
            ));
    }

    private Hero buildLoopHero() {
        return new Hero("loop", "Loop Hero", LearningPath.BRONZE, 6,
            "Repeats without getting tired",
            "A loop runs a block of code multiple times. Use for loops when you know how many times, while when you don't.",
            "for (int i = 0; i < 5; i++) {\n    System.out.println(i);\n}",
            List.of(
                q("What is the purpose of a loop?",
                    2,
                    a("To define a new variable"),
                    a("To handle errors"),
                    a("To run code multiple times")),
                q("Which loop is best when you know exactly how many iterations you need?",
                    0,
                    a("for loop"),
                    a("while loop"),
                    a("if loop")),
                q("What does i++ do in a for loop?",
                    1,
                    a("Resets i to zero"),
                    a("Increases i by one after each iteration"),
                    a("Stops the loop"))
            ));
    }

    private Hero buildMethodHero() {
        return new Hero("method", "Method Hero", LearningPath.BRONZE, 7,
            "Does one job, does it well",
            "A method is a named block of code you can call whenever needed. Methods keep your code organized and reusable.",
            "public int add(int a, int b) {\n    return a + b;\n}",
            List.of(
                q("What is a method in Java?",
                    0,
                    a("A named block of reusable code"),
                    a("A type of variable"),
                    a("A Java file structure")),
                q("What does the 'return' keyword do in a method?",
                    2,
                    a("Declares the method name"),
                    a("Starts the method body"),
                    a("Sends a value back to the caller")),
                q("What keyword is used for a method that returns nothing?",
                    1,
                    a("null"),
                    a("void"),
                    a("empty"))
            ));
    }

    // ---- SILVER ----

    private Hero buildClassHero() {
        return new Hero("class", "Class Hero", LearningPath.SILVER, 1,
            "The blueprint of everything",
            "A class is a blueprint for creating objects. It defines what an object knows and what it can do.",
            "public class Car {\n    String color;\n    void drive() { System.out.println(\"Vroom!\"); }\n}",
            List.of(
                q("What is a class in Java?",
                    1,
                    a("An instance of an object"),
                    a("A blueprint for creating objects"),
                    a("A method that creates variables")),
                q("What does a class define?",
                    2,
                    a("Only the data an object holds"),
                    a("Only the actions an object can perform"),
                    a("Both the data and behavior of objects")),
                q("Which keyword is used to define a class?",
                    0,
                    a("class"),
                    a("object"),
                    a("blueprint"))
            ));
    }

    private Hero buildObjectHero() {
        return new Hero("object", "Object Hero", LearningPath.SILVER, 2,
            "Brings blueprints to life",
            "An object is an instance of a class. You create objects from classes, each with their own data.",
            "Car myCar = new Car();\nmyCar.color = \"red\";\nmyCar.drive();",
            List.of(
                q("What is an object in Java?",
                    0,
                    a("An instance of a class"),
                    a("The same thing as a class"),
                    a("A method inside a class")),
                q("Which keyword is used to create a new object?",
                    2,
                    a("create"),
                    a("build"),
                    a("new")),
                q("Can two objects from the same class have different data?",
                    0,
                    a("Yes, each object has its own data"),
                    a("No, they always share the same data"),
                    a("Only if they use static fields"))
            ));
    }

    private Hero buildConstructorHero() {
        return new Hero("constructor", "Constructor Hero", LearningPath.SILVER, 3,
            "Sets the starting state",
            "A constructor is a special method that runs when an object is created. It sets up the initial state.",
            "public class Hero {\n    String name;\n    Hero(String name) { this.name = name; }\n}",
            List.of(
                q("When does a constructor run?",
                    1,
                    a("When a method is called on an object"),
                    a("When a new object is created with 'new'"),
                    a("When the class is defined")),
                q("What must a constructor's name match?",
                    0,
                    a("The class name"),
                    a("The first variable name"),
                    a("The package name")),
                q("What does 'this.name = name' do inside a constructor?",
                    2,
                    a("Creates a new variable called 'this'"),
                    a("Calls another constructor"),
                    a("Assigns the parameter to the object's field"))
            ));
    }

    private Hero buildCollectionHero() {
        return new Hero("collection", "Collection Hero", LearningPath.SILVER, 4,
            "Holds many things at once",
            "Collections store groups of objects. ArrayList is the most common, it grows and shrinks as needed.",
            "List<String> names = new ArrayList<>();\nnames.add(\"Alice\");\nnames.add(\"Bob\");",
            List.of(
                q("What is an ArrayList?",
                    2,
                    a("A fixed-size array"),
                    a("A type of database"),
                    a("A resizable list that can grow and shrink")),
                q("How do you add an element to an ArrayList?",
                    0,
                    a("names.add(\"Alice\")"),
                    a("names.push(\"Alice\")"),
                    a("names.insert(\"Alice\")")),
                q("What does List<String> tell the compiler?",
                    1,
                    a("The list can hold any type"),
                    a("The list can only hold String objects"),
                    a("The list has a fixed length"))
            ));
    }

    private Hero buildExceptionHero() {
        return new Hero("exception", "Exception Hero", LearningPath.SILVER, 5,
            "Handles the unexpected",
            "Exceptions signal that something went wrong. Use try-catch to handle errors gracefully without crashing.",
            "try {\n    int result = 10 / 0;\n} catch (ArithmeticException e) {\n    System.out.println(\"Cannot divide by zero.\");\n}",
            List.of(
                q("What does a try-catch block do?",
                    0,
                    a("Catches and handles runtime errors gracefully"),
                    a("Speeds up code execution"),
                    a("Declares new variables safely")),
                q("What happens if an exception is NOT caught?",
                    2,
                    a("The program continues normally"),
                    a("The exception is silently ignored"),
                    a("The program crashes with an error message")),
                q("Which exception is thrown when dividing by zero?",
                    1,
                    a("NullPointerException"),
                    a("ArithmeticException"),
                    a("IllegalArgumentException"))
            ));
    }

    // ---- GOLD ----

    private Hero buildInterfaceHero() {
        return new Hero("interface", "Interface Hero", LearningPath.GOLD, 1,
            "Defines what you can do",
            "An interface declares methods without implementing them. Classes implement interfaces to promise certain behavior.",
            "public interface Flyable {\n    void fly();\n}\npublic class Bird implements Flyable {\n    public void fly() { System.out.println(\"Flap!\"); }\n}",
            List.of(
                q("What does an interface define?",
                    1,
                    a("The full implementation of methods"),
                    a("Method signatures without implementations"),
                    a("Object instances directly")),
                q("Which keyword connects a class to an interface?",
                    2,
                    a("extends"),
                    a("uses"),
                    a("implements")),
                q("Why use interfaces?",
                    0,
                    a("To define a contract that classes must fulfill"),
                    a("To avoid writing any methods"),
                    a("To prevent inheritance"))
            ));
    }

    private Hero buildGenericHero() {
        return new Hero("generic", "Generic Hero", LearningPath.GOLD, 2,
            "Works with any type safely",
            "Generics let you write code that works with any type. They catch type errors at compile time instead of runtime.",
            "public class Box<T> {\n    T value;\n    Box(T value) { this.value = value; }\n}",
            List.of(
                q("What is the main benefit of generics?",
                    2,
                    a("They make code run faster"),
                    a("They reduce the number of classes you need"),
                    a("They catch type errors at compile time")),
                q("What does <T> represent in a generic class?",
                    0,
                    a("A type parameter that can be any type"),
                    a("A template file"),
                    a("A special Java keyword")),
                q("What happens if you use List<String> and try to add an Integer?",
                    1,
                    a("It works, Java converts the Integer automatically"),
                    a("A compile-time error occurs"),
                    a("A runtime exception is thrown"))
            ));
    }

    private Hero buildStreamHero() {
        return new Hero("stream", "Stream Hero", LearningPath.GOLD, 3,
            "Flows through data elegantly",
            "Streams process collections with a pipeline of operations. Chain filter, map, and collect to transform data cleanly.",
            "List<String> result = names.stream()\n    .filter(n -> n.startsWith(\"A\"))\n    .collect(Collectors.toList());",
            List.of(
                q("What does stream().filter() do?",
                    0,
                    a("Keeps only elements matching a condition"),
                    a("Sorts the elements"),
                    a("Converts elements to a different type")),
                q("What is the purpose of collect() in a stream pipeline?",
                    2,
                    a("It starts the stream"),
                    a("It filters elements"),
                    a("It gathers the stream results into a collection")),
                q("Are Java streams lazy or eager by default?",
                    0,
                    a("Lazy - they only process elements when a terminal operation is called"),
                    a("Eager - they process all elements immediately"),
                    a("Neither - they process elements randomly"))
            ));
    }

    private Hero buildLambdaHero() {
        return new Hero("lambda", "Lambda Hero", LearningPath.GOLD, 4,
            "A function without a name",
            "A lambda is a short anonymous function. Use lambdas to pass behavior as a value, especially with streams.",
            "Runnable r = () -> System.out.println(\"Hello!\");\nr.run();",
            List.of(
                q("What is a lambda expression?",
                    1,
                    a("A named method in a class"),
                    a("An anonymous function that can be passed as a value"),
                    a("A type of Java loop")),
                q("What does the -> symbol mean in a lambda?",
                    0,
                    a("Separates the parameters from the body"),
                    a("Creates a new object"),
                    a("Compares two values")),
                q("Which functional interface can hold a lambda with no parameters and no return value?",
                    2,
                    a("Comparator"),
                    a("Function"),
                    a("Runnable"))
            ));
    }

    private Hero buildOptionalHero() {
        return new Hero("optional", "Optional Hero", LearningPath.GOLD, 5,
            "Maybe something, maybe nothing",
            "Optional wraps a value that might be absent. It forces you to handle the null case explicitly.",
            "Optional<String> name = Optional.of(\"Alex\");\nname.ifPresent(n -> System.out.println(n));",
            List.of(
                q("Why was Optional introduced in Java?",
                    1,
                    a("To replace all String variables"),
                    a("To explicitly represent values that might be absent"),
                    a("To speed up method calls")),
                q("What does Optional.empty() return?",
                    0,
                    a("An Optional with no value inside"),
                    a("null directly"),
                    a("An Optional containing zero")),
                q("What does ifPresent() do?",
                    2,
                    a("Throws an exception if the value is missing"),
                    a("Always runs the provided action"),
                    a("Runs the provided action only if a value is present"))
            ));
    }

    private Hero buildRecordHero() {
        return new Hero("record", "Record Hero", LearningPath.GOLD, 6,
            "Data without the boilerplate",
            "A record is an immutable data class. Java generates equals, hashCode, and toString automatically.",
            "public record Point(int x, int y) {}\nPoint p = new Point(3, 4);\nSystem.out.println(p.x());",
            List.of(
                q("What does Java automatically generate for a record?",
                    2,
                    a("Only getters"),
                    a("Only toString"),
                    a("equals, hashCode, toString, and accessor methods")),
                q("Are records mutable or immutable?",
                    1,
                    a("Mutable - you can change their fields"),
                    a("Immutable - fields are set once at creation"),
                    a("It depends on the field type")),
                q("How do you access the field 'x' from a record named Point?",
                    0,
                    a("point.x()"),
                    a("point.getX()"),
                    a("point.x"))
            ));
    }

    // ---- SPRING MASTER ----

    private Hero buildControllerHero() {
        return new Hero("controller", "Controller Hero", LearningPath.SPRING_MASTER, 1,
            "Handles web requests",
            "A @Controller maps HTTP requests to Java methods. It connects the web layer to your application logic.",
            "@Controller\npublic class HelloController {\n    @GetMapping(\"/hello\")\n    public String hello() { return \"hello\"; }\n}",
            List.of(
                q("What does @Controller do in Spring?",
                    0,
                    a("Marks a class as a web request handler"),
                    a("Marks a class as a database repository"),
                    a("Makes a class into a background service")),
                q("What does @GetMapping(\"/hello\") do?",
                    1,
                    a("Creates a new GET request"),
                    a("Maps HTTP GET requests to /hello to this method"),
                    a("Sends a GET request to an external URL")),
                q("What does a controller method return in Spring MVC with Thymeleaf?",
                    2,
                    a("A JSON object"),
                    a("An HTTP status code"),
                    a("The name of a template to render"))
            ));
    }

    private Hero buildServiceHero() {
        return new Hero("service", "Service Hero", LearningPath.SPRING_MASTER, 2,
            "Where business logic lives",
            "A @Service holds the core logic of your app. Controllers call services, keeping layers separate.",
            "@Service\npublic class HeroService {\n    public String greet(String name) {\n        return \"Hello, \" + name;\n    }\n}",
            List.of(
                q("What is the purpose of the @Service annotation?",
                    1,
                    a("To handle HTTP requests"),
                    a("To mark a class as containing business logic"),
                    a("To connect to a database")),
                q("Why should controllers call services instead of containing business logic themselves?",
                    0,
                    a("To keep layers separated and code organized"),
                    a("Because controllers cannot contain methods"),
                    a("Because services run faster")),
                q("Which Spring layer should contain database access code?",
                    2,
                    a("@Controller"),
                    a("@Service"),
                    a("@Repository"))
            ));
    }

    private Hero buildRepositoryHero() {
        return new Hero("repository", "Repository Hero", LearningPath.SPRING_MASTER, 3,
            "Talks to the database",
            "A @Repository handles data access. Spring Data can generate CRUD operations automatically.",
            "@Repository\npublic interface HeroRepo extends JpaRepository<Hero, Long> {}",
            List.of(
                q("What does @Repository do?",
                    2,
                    a("Marks a class as a web controller"),
                    a("Marks a class as a service bean"),
                    a("Marks a class as a data access component")),
                q("What does extending JpaRepository provide?",
                    0,
                    a("Ready-made CRUD methods like save(), findById(), deleteById()"),
                    a("HTTP endpoint mappings"),
                    a("Transaction management only")),
                q("Where in the Spring layered architecture does the Repository sit?",
                    1,
                    a("The web layer"),
                    a("The data layer"),
                    a("The service layer"))
            ));
    }

    private Hero buildBeanHero() {
        return new Hero("bean", "Bean Hero", LearningPath.SPRING_MASTER, 4,
            "Lives in the Spring container",
            "A bean is an object managed by Spring. Spring creates beans, wires them together, and manages their lifecycle.",
            "@Bean\npublic Clock clock() {\n    return Clock.systemDefaultZone();\n}",
            List.of(
                q("What is a Spring bean?",
                    0,
                    a("An object whose lifecycle is managed by the Spring container"),
                    a("A Java primitive type"),
                    a("A type of Spring annotation")),
                q("Which annotation declares a method that returns a Spring bean?",
                    1,
                    a("@Component"),
                    a("@Bean"),
                    a("@Service")),
                q("What is the Spring IoC container responsible for?",
                    2,
                    a("Handling HTTP requests"),
                    a("Writing to the database"),
                    a("Creating and wiring beans together"))
            ));
    }

    private Hero buildDependencyInjectionHero() {
        return new Hero("dependency-injection", "Dependency Injection Hero", LearningPath.SPRING_MASTER, 5,
            "Gets what it needs automatically",
            "Dependency Injection lets Spring provide objects to your classes. Use @Autowired or constructor injection.",
            "@Service\npublic class OrderService {\n    private final PaymentService payments;\n    OrderService(PaymentService payments) {\n        this.payments = payments;\n    }\n}",
            List.of(
                q("What is Dependency Injection?",
                    1,
                    a("Creating objects manually with 'new'"),
                    a("Having Spring automatically provide required objects to a class"),
                    a("Injecting code into running programs")),
                q("Why is constructor injection preferred over field injection?",
                    0,
                    a("It makes dependencies explicit and the class easier to test"),
                    a("It requires less code"),
                    a("It only works with @Autowired")),
                q("What happens if Spring cannot find a matching bean to inject?",
                    2,
                    a("Spring injects null instead"),
                    a("The method is skipped"),
                    a("The application fails to start with an error"))
            ));
    }
}
