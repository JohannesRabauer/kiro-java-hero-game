package com.javahero.config;

import com.javahero.model.*;
import com.javahero.repository.HeroCardRepository;
import com.javahero.repository.HeroContentRepository;
import com.javahero.repository.QuizQuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final HeroCardRepository heroCardRepository;
    private final HeroContentRepository heroContentRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    public DataInitializer(HeroCardRepository heroCardRepository,
                           HeroContentRepository heroContentRepository,
                           QuizQuestionRepository quizQuestionRepository) {
        this.heroCardRepository = heroCardRepository;
        this.heroContentRepository = heroContentRepository;
        this.quizQuestionRepository = quizQuestionRepository;
    }

    @Override
    public void run(String... args) {
        if (heroCardRepository.count() > 0) {
            return;
        }
        seedBronzePath();
        seedSilverPath();
        seedGoldPath();
        seedSpringMasterPath();
    }

    private void seedBronzePath() {
        createHero("bronze-variable", "Variable Hero", "Variables",
                LearningPath.BRONZE, 1);
        createHero("bronze-string", "String Hero", "Strings",
                LearningPath.BRONZE, 2);
        createHero("bronze-number", "Number Hero", "Numbers",
                LearningPath.BRONZE, 3);
        createHero("bronze-boolean", "Boolean Hero", "Booleans",
                LearningPath.BRONZE, 4);
        createHero("bronze-if", "If Hero", "If Statements",
                LearningPath.BRONZE, 5);
        createHero("bronze-loop", "Loop Hero", "Loops",
                LearningPath.BRONZE, 6);
        createHero("bronze-method", "Method Hero", "Methods",
                LearningPath.BRONZE, 7);

        seedBronzeContent();
        seedBronzeQuizzes();
    }

    private void seedSilverPath() {
        createHero("silver-class", "Class Hero", "Classes",
                LearningPath.SILVER, 1);
        createHero("silver-object", "Object Hero", "Objects",
                LearningPath.SILVER, 2);
        createHero("silver-constructor", "Constructor Hero", "Constructors",
                LearningPath.SILVER, 3);
        createHero("silver-collection", "Collection Hero", "Collections",
                LearningPath.SILVER, 4);
        createHero("silver-exception", "Exception Hero", "Exceptions",
                LearningPath.SILVER, 5);

        seedSilverContent();
        seedSilverQuizzes();
    }

    private void seedGoldPath() {
        createHero("gold-interface", "Interface Hero", "Interfaces",
                LearningPath.GOLD, 1);
        createHero("gold-generic", "Generic Hero", "Generics",
                LearningPath.GOLD, 2);
        createHero("gold-stream", "Stream Hero", "Streams",
                LearningPath.GOLD, 3);
        createHero("gold-lambda", "Lambda Hero", "Lambdas",
                LearningPath.GOLD, 4);
        createHero("gold-optional", "Optional Hero", "Optionals",
                LearningPath.GOLD, 5);
        createHero("gold-record", "Record Hero", "Records",
                LearningPath.GOLD, 6);

        seedGoldContent();
        seedGoldQuizzes();
    }

    private void seedSpringMasterPath() {
        createHero("spring-controller", "Controller Hero", "Controllers",
                LearningPath.SPRING_MASTER, 1);
        createHero("spring-service", "Service Hero", "Services",
                LearningPath.SPRING_MASTER, 2);
        createHero("spring-repository", "Repository Hero", "Repositories",
                LearningPath.SPRING_MASTER, 3);
        createHero("spring-bean", "Bean Hero", "Beans",
                LearningPath.SPRING_MASTER, 4);
        createHero("spring-di", "DI Hero", "Dependency Injection",
                LearningPath.SPRING_MASTER, 5);

        seedSpringMasterContent();
        seedSpringMasterQuizzes();
    }

    private void createHero(String id, String name, String conceptTitle,
                            LearningPath path, int order) {
        HeroCard card = new HeroCard();
        card.setId(id);
        card.setName(name);
        card.setConceptTitle(conceptTitle);
        card.setLearningPath(path);
        card.setOrderInPath(order);
        heroCardRepository.save(card);
    }

    private HeroContent createContent(String heroId, String whatItIs,
                                      String whyItMatters, String howToUseIt,
                                      String exampleTitle, String exampleCode) {
        HeroContent content = new HeroContent();
        content.setHeroId(heroId);
        content.setWhatItIs(whatItIs);
        content.setWhyItMatters(whyItMatters);
        content.setHowToUseIt(howToUseIt);

        CodeExample example = new CodeExample();
        example.setTitle(exampleTitle);
        example.setCode(exampleCode);
        example.setLanguage("java");

        List<CodeExample> examples = new ArrayList<>();
        examples.add(example);
        content.setCodeExamples(examples);

        return heroContentRepository.save(content);
    }

    private void createQuiz(String heroId, String questionId,
                            String questionText, String correctId,
                            String[][] options) {
        QuizQuestion q = new QuizQuestion();
        q.setId(questionId);
        q.setHeroId(heroId);
        q.setQuestionText(questionText);
        q.setCorrectOptionId(correctId);

        List<AnswerOption> opts = new ArrayList<>();
        for (String[] opt : options) {
            AnswerOption ao = new AnswerOption();
            ao.setId(opt[0]);
            ao.setText(opt[1]);
            opts.add(ao);
        }
        q.setOptions(opts);
        quizQuestionRepository.save(q);
    }

    // ========== BRONZE CONTENT ==========

    private void seedBronzeContent() {
        createContent("bronze-variable",
            "A variable is a named container that stores a value in memory. In Java, every variable has a type that determines what kind of data it can hold.",
            "Variables are the foundation of all programs. They let you store, retrieve, and manipulate data. Without variables, programs couldn't remember anything between steps.",
            "Declare a variable with a type and name, then assign a value using the equals sign. You can change the value later as long as the new value matches the type.",
            "Declaring Variables",
            "int age = 25;\nString name = \"Alice\";\ndouble price = 9.99;\nboolean active = true;");

        createContent("bronze-string",
            "A String is a sequence of characters enclosed in double quotes. In Java, String is an object, not a primitive type, and it is immutable once created.",
            "Strings are used everywhere: user input, file names, messages, and data processing. Understanding strings is essential because nearly every program handles text.",
            "Create strings with double quotes. Use the + operator to concatenate. Call methods like length(), substring(), and toUpperCase() to manipulate text.",
            "Working with Strings",
            "String greeting = \"Hello\";\nString name = \"World\";\nString message = greeting + \", \" + name + \"!\";\nint len = message.length(); // 13");

        createContent("bronze-number",
            "Numbers in Java come in two forms: integers (int, long) for whole numbers and floating-point (double, float) for decimals. Each type has a fixed size in memory.",
            "Programs constantly work with numbers for calculations, counting, indexing, and measurements. Choosing the right number type affects precision and memory usage.",
            "Use int for most whole numbers and double for decimals. Java provides arithmetic operators (+, -, *, /, %) and Math class methods for complex calculations.",
            "Number Operations",
            "int count = 10;\ndouble price = 19.99;\nint total = count * 2; // 20\ndouble tax = price * 0.07; // 1.3993");

        createContent("bronze-boolean",
            "A boolean is a primitive type that holds only two values: true or false. It is named after mathematician George Boole and is the basis of logical operations.",
            "Booleans control program flow through conditions and loops. Every if statement and while loop relies on boolean expressions to decide what code to execute.",
            "Declare booleans directly or derive them from comparisons. Combine with logical operators && (and), || (or), and ! (not) to build complex conditions.",
            "Boolean Logic",
            "boolean isAdult = age >= 18;\nboolean hasTicket = true;\nboolean canEnter = isAdult && hasTicket;\nif (canEnter) {\n    System.out.println(\"Welcome!\");\n}");

        createContent("bronze-if",
            "An if statement evaluates a boolean condition and executes a block of code only if the condition is true. You can add else and else-if branches for alternative paths.",
            "Programs must make decisions. If statements let your code respond differently to different situations, making programs dynamic and intelligent rather than purely sequential.",
            "Write the condition in parentheses after if. Use curly braces for the code block. Add else for the alternative path and else-if for multiple conditions.",
            "Conditional Branching",
            "int score = 85;\nif (score >= 90) {\n    System.out.println(\"A grade\");\n} else if (score >= 80) {\n    System.out.println(\"B grade\");\n} else {\n    System.out.println(\"Keep trying\");\n}");

        createContent("bronze-loop",
            "A loop repeats a block of code multiple times. Java offers for loops for counted iteration, while loops for condition-based repetition, and for-each loops for collections.",
            "Loops eliminate repetitive code and let programs process large amounts of data efficiently. Almost every real program uses loops to iterate over lists, retry operations, or wait for events.",
            "Use for when you know how many times to repeat. Use while when you repeat until a condition changes. Use for-each to iterate over arrays or collections.",
            "Loop Types",
            "for (int i = 0; i < 5; i++) {\n    System.out.println(i);\n}\n\nString[] names = {\"A\", \"B\", \"C\"};\nfor (String n : names) {\n    System.out.println(n);\n}");

        createContent("bronze-method",
            "A method is a named block of code that performs a specific task. Methods can accept parameters, perform operations, and return a result. They belong to a class.",
            "Methods organize code into reusable pieces. Instead of copying the same logic in multiple places, you define it once in a method and call it whenever needed.",
            "Define a method with a return type, name, and parameters. Call it by name with arguments in parentheses. Use void for methods that don't return a value.",
            "Defining Methods",
            "public int add(int a, int b) {\n    return a + b;\n}\n\npublic void greet(String name) {\n    System.out.println(\"Hello, \" + name);\n}\n\nint sum = add(3, 4); // 7\ngreet(\"Alice\");");
    }

    // ========== BRONZE QUIZZES ==========

    private void seedBronzeQuizzes() {
        // Variable quizzes
        createQuiz("bronze-variable", "bv-q1",
            "What keyword is used to declare an integer variable in Java?",
            "bv-q1-a", new String[][]{
                {"bv-q1-a", "int"},
                {"bv-q1-b", "integer"},
                {"bv-q1-c", "num"},
                {"bv-q1-d", "var"}});
        createQuiz("bronze-variable", "bv-q2",
            "Which statement correctly declares and initializes a variable?",
            "bv-q2-b", new String[][]{
                {"bv-q2-a", "int = 5;"},
                {"bv-q2-b", "int x = 5;"},
                {"bv-q2-c", "x int = 5;"}});
        createQuiz("bronze-variable", "bv-q3",
            "What happens if you assign a String to an int variable?",
            "bv-q3-c", new String[][]{
                {"bv-q3-a", "It works fine"},
                {"bv-q3-b", "The value becomes 0"},
                {"bv-q3-c", "A compilation error occurs"}});

        // String quizzes
        createQuiz("bronze-string", "bs-q1",
            "How are Strings declared in Java?",
            "bs-q1-a", new String[][]{
                {"bs-q1-a", "With double quotes: \"text\""},
                {"bs-q1-b", "With single quotes: 'text'"},
                {"bs-q1-c", "With backticks: `text`"}});
        createQuiz("bronze-string", "bs-q2",
            "What does the length() method return?",
            "bs-q2-b", new String[][]{
                {"bs-q2-a", "The number of words"},
                {"bs-q2-b", "The number of characters"},
                {"bs-q2-c", "The memory size in bytes"}});
        createQuiz("bronze-string", "bs-q3",
            "Are Java Strings mutable or immutable?",
            "bs-q3-b", new String[][]{
                {"bs-q3-a", "Mutable"},
                {"bs-q3-b", "Immutable"},
                {"bs-q3-c", "Depends on the content"}});

        // Number quizzes
        createQuiz("bronze-number", "bn-q1",
            "Which type is best for storing decimal values like 3.14?",
            "bn-q1-b", new String[][]{
                {"bn-q1-a", "int"},
                {"bn-q1-b", "double"},
                {"bn-q1-c", "boolean"},
                {"bn-q1-d", "String"}});
        createQuiz("bronze-number", "bn-q2",
            "What is the result of 7 / 2 in Java when both are int?",
            "bn-q2-a", new String[][]{
                {"bn-q2-a", "3"},
                {"bn-q2-b", "3.5"},
                {"bn-q2-c", "4"}});
        createQuiz("bronze-number", "bn-q3",
            "What does the % operator do?",
            "bn-q3-c", new String[][]{
                {"bn-q3-a", "Calculates a percentage"},
                {"bn-q3-b", "Divides two numbers"},
                {"bn-q3-c", "Returns the remainder of division"}});

        // Boolean quizzes
        createQuiz("bronze-boolean", "bb-q1",
            "What are the two possible values of a boolean?",
            "bb-q1-a", new String[][]{
                {"bb-q1-a", "true and false"},
                {"bb-q1-b", "0 and 1"},
                {"bb-q1-c", "yes and no"}});
        createQuiz("bronze-boolean", "bb-q2",
            "What does the && operator represent?",
            "bb-q2-b", new String[][]{
                {"bb-q2-a", "Logical OR"},
                {"bb-q2-b", "Logical AND"},
                {"bb-q2-c", "Logical NOT"}});
        createQuiz("bronze-boolean", "bb-q3",
            "What does !true evaluate to?",
            "bb-q3-a", new String[][]{
                {"bb-q3-a", "false"},
                {"bb-q3-b", "true"},
                {"bb-q3-c", "null"}});

        // If quizzes
        createQuiz("bronze-if", "bi-q1",
            "What type of expression goes inside the if parentheses?",
            "bi-q1-c", new String[][]{
                {"bi-q1-a", "An integer"},
                {"bi-q1-b", "A String"},
                {"bi-q1-c", "A boolean expression"}});
        createQuiz("bronze-if", "bi-q2",
            "When does the else block execute?",
            "bi-q2-a", new String[][]{
                {"bi-q2-a", "When the if condition is false"},
                {"bi-q2-b", "When the if condition is true"},
                {"bi-q2-c", "Always after the if block"}});
        createQuiz("bronze-if", "bi-q3",
            "What is the purpose of else-if?",
            "bi-q3-b", new String[][]{
                {"bi-q3-a", "To create a loop"},
                {"bi-q3-b", "To test additional conditions"},
                {"bi-q3-c", "To end the program"}});

        // Loop quizzes
        createQuiz("bronze-loop", "bl-q1",
            "Which loop is best when you know the exact number of iterations?",
            "bl-q1-a", new String[][]{
                {"bl-q1-a", "for loop"},
                {"bl-q1-b", "while loop"},
                {"bl-q1-c", "do-while loop"}});
        createQuiz("bronze-loop", "bl-q2",
            "What happens if a while loop condition never becomes false?",
            "bl-q2-b", new String[][]{
                {"bl-q2-a", "The program stops"},
                {"bl-q2-b", "An infinite loop occurs"},
                {"bl-q2-c", "Java throws an exception"}});
        createQuiz("bronze-loop", "bl-q3",
            "What does the enhanced for-each loop iterate over?",
            "bl-q3-c", new String[][]{
                {"bl-q3-a", "Only integers"},
                {"bl-q3-b", "Only Strings"},
                {"bl-q3-c", "Arrays and collections"}});

        // Method quizzes
        createQuiz("bronze-method", "bm-q1",
            "What keyword indicates a method returns nothing?",
            "bm-q1-b", new String[][]{
                {"bm-q1-a", "null"},
                {"bm-q1-b", "void"},
                {"bm-q1-c", "empty"},
                {"bm-q1-d", "none"}});
        createQuiz("bronze-method", "bm-q2",
            "What is the purpose of the return statement?",
            "bm-q2-a", new String[][]{
                {"bm-q2-a", "To send a value back to the caller"},
                {"bm-q2-b", "To print output to the console"},
                {"bm-q2-c", "To end the program"}});
        createQuiz("bronze-method", "bm-q3",
            "What are method parameters?",
            "bm-q3-c", new String[][]{
                {"bm-q3-a", "Return values"},
                {"bm-q3-b", "Local variables declared inside the method"},
                {"bm-q3-c", "Values passed into the method when called"}});
    }

    // ========== SILVER CONTENT ==========

    private void seedSilverContent() {
        createContent("silver-class",
            "A class is a blueprint for creating objects. It defines the attributes (fields) and behaviors (methods) that objects of that type will have.",
            "Classes are the core building block of object-oriented programming. They let you model real-world concepts, group related data and behavior, and create reusable code structures.",
            "Define a class with the class keyword. Add fields to store state and methods to define behavior. Create instances using the new keyword.",
            "Defining a Class",
            "public class Dog {\n    String name;\n    int age;\n\n    public void bark() {\n        System.out.println(name + \" says Woof!\");\n    }\n}");

        createContent("silver-object",
            "An object is an instance of a class. When you create an object with new, Java allocates memory for its fields and you can call its methods.",
            "Objects let you create multiple independent instances from one blueprint. Each object has its own state, enabling you to model collections of similar things with different data.",
            "Use the new keyword followed by the constructor to create an object. Access fields and methods with the dot operator.",
            "Creating Objects",
            "Dog myDog = new Dog();\nmyDog.name = \"Rex\";\nmyDog.age = 3;\nmyDog.bark(); // Rex says Woof!");

        createContent("silver-constructor",
            "A constructor is a special method called when an object is created. It has the same name as the class and no return type. It initializes the object's state.",
            "Constructors ensure objects start in a valid state. Without them, you'd have to set every field manually after creation, risking incomplete or inconsistent objects.",
            "Define a constructor matching the class name. Add parameters for required initial values. Java provides a default no-arg constructor if you don't define any.",
            "Using Constructors",
            "public class Dog {\n    String name;\n    int age;\n\n    public Dog(String name, int age) {\n        this.name = name;\n        this.age = age;\n    }\n}\n\nDog rex = new Dog(\"Rex\", 3);");

        createContent("silver-collection",
            "A Collection is a framework of classes and interfaces for storing groups of objects. Common types include List (ordered), Set (unique), and Map (key-value pairs).",
            "Collections replace fixed-size arrays with dynamic, flexible containers. They provide built-in methods for searching, sorting, and iterating that save you from writing low-level code.",
            "Use ArrayList for ordered lists, HashSet for unique elements, and HashMap for key-value lookups. Import from java.util and use generics to specify element types.",
            "Collections in Action",
            "List<String> names = new ArrayList<>();\nnames.add(\"Alice\");\nnames.add(\"Bob\");\nnames.size(); // 2\n\nMap<String, Integer> ages = new HashMap<>();\nages.put(\"Alice\", 30);");

        createContent("silver-exception",
            "An exception is an event that disrupts normal program flow. Java uses try-catch blocks to handle exceptions gracefully instead of crashing the program.",
            "Exceptions let your program recover from errors like invalid input, missing files, or network failures. Proper exception handling makes software robust and user-friendly.",
            "Wrap risky code in a try block and catch specific exceptions. Use finally for cleanup. Throw your own exceptions with the throw keyword.",
            "Exception Handling",
            "try {\n    int result = 10 / 0;\n} catch (ArithmeticException e) {\n    System.out.println(\"Cannot divide by zero\");\n} finally {\n    System.out.println(\"Done\");\n}");
    }

    // ========== SILVER QUIZZES ==========

    private void seedSilverQuizzes() {
        // Class quizzes
        createQuiz("silver-class", "sc-q1",
            "What keyword defines a class in Java?",
            "sc-q1-b", new String[][]{
                {"sc-q1-a", "object"},
                {"sc-q1-b", "class"},
                {"sc-q1-c", "struct"},
                {"sc-q1-d", "type"}});
        createQuiz("silver-class", "sc-q2",
            "What do fields in a class represent?",
            "sc-q2-a", new String[][]{
                {"sc-q2-a", "The state or data of objects"},
                {"sc-q2-b", "The methods of objects"},
                {"sc-q2-c", "The class name"}});
        createQuiz("silver-class", "sc-q3",
            "How do you create a new instance of a class?",
            "sc-q3-c", new String[][]{
                {"sc-q3-a", "By calling the class name as a function"},
                {"sc-q3-b", "Using the create keyword"},
                {"sc-q3-c", "Using the new keyword"}});

        // Object quizzes
        createQuiz("silver-object", "so-q1",
            "What is the relationship between a class and an object?",
            "so-q1-a", new String[][]{
                {"so-q1-a", "A class is a blueprint, an object is an instance"},
                {"so-q1-b", "They are the same thing"},
                {"so-q1-c", "An object defines a class"}});
        createQuiz("silver-object", "so-q2",
            "Which operator is used to access an object's fields?",
            "so-q2-b", new String[][]{
                {"so-q2-a", "::"},
                {"so-q2-b", "."},
                {"so-q2-c", "->"}});
        createQuiz("silver-object", "so-q3",
            "Can multiple objects be created from the same class?",
            "so-q3-a", new String[][]{
                {"so-q3-a", "Yes, each with its own state"},
                {"so-q3-b", "No, only one object per class"},
                {"so-q3-c", "Yes, but they all share the same state"}});

        // Constructor quizzes
        createQuiz("silver-constructor", "scon-q1",
            "What is a constructor's return type?",
            "scon-q1-c", new String[][]{
                {"scon-q1-a", "void"},
                {"scon-q1-b", "The class type"},
                {"scon-q1-c", "It has no return type"}});
        createQuiz("silver-constructor", "scon-q2",
            "When is a constructor called?",
            "scon-q2-a", new String[][]{
                {"scon-q2-a", "When an object is created with new"},
                {"scon-q2-b", "When a method is called"},
                {"scon-q2-c", "When the program starts"}});
        createQuiz("silver-constructor", "scon-q3",
            "What does 'this' refer to inside a constructor?",
            "scon-q3-b", new String[][]{
                {"scon-q3-a", "The class itself"},
                {"scon-q3-b", "The current object being created"},
                {"scon-q3-c", "The parent class"}});

        // Collection quizzes
        createQuiz("silver-collection", "scol-q1",
            "Which Collection type maintains insertion order and allows duplicates?",
            "scol-q1-a", new String[][]{
                {"scol-q1-a", "List"},
                {"scol-q1-b", "Set"},
                {"scol-q1-c", "Map"}});
        createQuiz("silver-collection", "scol-q2",
            "What does a Map store?",
            "scol-q2-b", new String[][]{
                {"scol-q2-a", "Only unique values"},
                {"scol-q2-b", "Key-value pairs"},
                {"scol-q2-c", "Ordered elements"}});
        createQuiz("silver-collection", "scol-q3",
            "What method adds an element to an ArrayList?",
            "scol-q3-c", new String[][]{
                {"scol-q3-a", "insert()"},
                {"scol-q3-b", "push()"},
                {"scol-q3-c", "add()"}});

        // Exception quizzes
        createQuiz("silver-exception", "se-q1",
            "What block is used to handle exceptions?",
            "se-q1-b", new String[][]{
                {"se-q1-a", "if-else"},
                {"se-q1-b", "try-catch"},
                {"se-q1-c", "switch-case"}});
        createQuiz("silver-exception", "se-q2",
            "What does the finally block do?",
            "se-q2-a", new String[][]{
                {"se-q2-a", "Executes whether or not an exception occurred"},
                {"se-q2-b", "Executes only when an exception occurs"},
                {"se-q2-c", "Stops the program"}});
        createQuiz("silver-exception", "se-q3",
            "Which keyword throws an exception manually?",
            "se-q3-c", new String[][]{
                {"se-q3-a", "error"},
                {"se-q3-b", "raise"},
                {"se-q3-c", "throw"}});
    }

    // ========== GOLD CONTENT ==========

    private void seedGoldContent() {
        createContent("gold-interface",
            "An interface defines a contract of methods that implementing classes must provide. It contains method signatures without implementations (before Java 8) or with default implementations.",
            "Interfaces enable polymorphism and loose coupling. They let you write code that depends on behavior rather than specific classes, making systems flexible and testable.",
            "Define an interface with the interface keyword. Classes implement it using the implements keyword and must provide all declared methods.",
            "Implementing Interfaces",
            "public interface Flyable {\n    void fly();\n}\n\npublic class Bird implements Flyable {\n    public void fly() {\n        System.out.println(\"Flapping wings\");\n    }\n}");

        createContent("gold-generic",
            "Generics let you write classes and methods that work with any type while maintaining compile-time type safety. They use type parameters like <T> as placeholders for actual types.",
            "Generics eliminate unsafe casts and catch type errors at compile time. They enable reusable data structures like List<String> that are both flexible and type-safe.",
            "Add type parameters in angle brackets after the class or method name. Use meaningful names like T for type, E for element, K for key, V for value.",
            "Generic Class",
            "public class Box<T> {\n    private T value;\n\n    public void set(T value) { this.value = value; }\n    public T get() { return value; }\n}\n\nBox<String> box = new Box<>();\nbox.set(\"Hello\");");

        createContent("gold-stream",
            "A Stream is a sequence of elements that supports functional-style operations like filter, map, and reduce. Streams process data declaratively without modifying the source collection.",
            "Streams simplify data processing by replacing verbose loops with concise, readable pipelines. They also enable easy parallelization for performance-critical operations.",
            "Create a stream from a collection with .stream(). Chain intermediate operations (filter, map) and terminate with a final operation (collect, forEach, count).",
            "Stream Pipeline",
            "List<String> names = List.of(\"Alice\", \"Bob\", \"Charlie\");\nList<String> result = names.stream()\n    .filter(n -> n.length() > 3)\n    .map(String::toUpperCase)\n    .collect(Collectors.toList());");

        createContent("gold-lambda",
            "A lambda expression is a concise way to represent an anonymous function. It provides a clear syntax for implementing functional interfaces with a single abstract method.",
            "Lambdas reduce boilerplate code dramatically. Instead of writing full anonymous classes, you express behavior in one line, making code more readable and functional.",
            "Write lambdas with parameters, an arrow ->, and a body. Use them wherever a functional interface is expected, such as in stream operations or event handlers.",
            "Lambda Syntax",
            "// Full form\nComparator<String> comp = (a, b) -> a.compareTo(b);\n\n// With streams\nList<Integer> nums = List.of(1, 2, 3, 4, 5);\nnums.stream()\n    .filter(n -> n % 2 == 0)\n    .forEach(System.out::println);");

        createContent("gold-optional",
            "Optional is a container that may or may not hold a non-null value. It was introduced in Java 8 to provide a better alternative to returning null from methods.",
            "Optional makes null handling explicit and reduces NullPointerException risks. It forces callers to consciously handle the absence of a value instead of forgetting a null check.",
            "Create with Optional.of(), Optional.ofNullable(), or Optional.empty(). Use methods like isPresent(), orElse(), and map() to safely work with the value.",
            "Using Optional",
            "Optional<String> name = Optional.ofNullable(findName());\nString result = name.orElse(\"Unknown\");\n\nname.ifPresent(n -> System.out.println(\"Found: \" + n));\n\nOptional<Integer> len = name.map(String::length);");

        createContent("gold-record",
            "A record is a special class introduced in Java 16 that provides a compact syntax for immutable data carriers. It auto-generates constructors, getters, equals, hashCode, and toString.",
            "Records eliminate boilerplate for simple data classes. Instead of writing dozens of lines for a POJO, a record defines the same structure in one line with full functionality.",
            "Define a record with the record keyword followed by component names in parentheses. Access components via auto-generated accessor methods (no get prefix).",
            "Record Definition",
            "public record Point(int x, int y) {}\n\nPoint p = new Point(3, 4);\nint x = p.x(); // 3\nint y = p.y(); // 4\nSystem.out.println(p); // Point[x=3, y=4]");
    }

    // ========== GOLD QUIZZES ==========

    private void seedGoldQuizzes() {
        // Interface quizzes
        createQuiz("gold-interface", "gi-q1",
            "What keyword does a class use to adopt an interface?",
            "gi-q1-b", new String[][]{
                {"gi-q1-a", "extends"},
                {"gi-q1-b", "implements"},
                {"gi-q1-c", "inherits"}});
        createQuiz("gold-interface", "gi-q2",
            "Can a class implement multiple interfaces?",
            "gi-q2-a", new String[][]{
                {"gi-q2-a", "Yes"},
                {"gi-q2-b", "No"},
                {"gi-q2-c", "Only if they have no methods"}});
        createQuiz("gold-interface", "gi-q3",
            "What happens if a class doesn't implement all interface methods?",
            "gi-q3-c", new String[][]{
                {"gi-q3-a", "The methods return null"},
                {"gi-q3-b", "A runtime error occurs"},
                {"gi-q3-c", "A compilation error occurs"}});

        // Generic quizzes
        createQuiz("gold-generic", "gg-q1",
            "What do angle brackets <T> represent in generics?",
            "gg-q1-b", new String[][]{
                {"gg-q1-a", "An array type"},
                {"gg-q1-b", "A type parameter placeholder"},
                {"gg-q1-c", "A comparison operator"}});
        createQuiz("gold-generic", "gg-q2",
            "What is the main benefit of generics?",
            "gg-q2-a", new String[][]{
                {"gg-q2-a", "Compile-time type safety"},
                {"gg-q2-b", "Faster runtime performance"},
                {"gg-q2-c", "Smaller code size"}});
        createQuiz("gold-generic", "gg-q3",
            "Can you use primitive types like int as generic type parameters?",
            "gg-q3-b", new String[][]{
                {"gg-q3-a", "Yes"},
                {"gg-q3-b", "No, you must use wrapper types like Integer"},
                {"gg-q3-c", "Only in Java 17+"}});

        // Stream quizzes
        createQuiz("gold-stream", "gs-q1",
            "Which method starts a stream from a List?",
            "gs-q1-c", new String[][]{
                {"gs-q1-a", "list.toStream()"},
                {"gs-q1-b", "Stream.from(list)"},
                {"gs-q1-c", "list.stream()"}});
        createQuiz("gold-stream", "gs-q2",
            "What type of operation is filter()?",
            "gs-q2-a", new String[][]{
                {"gs-q2-a", "Intermediate operation"},
                {"gs-q2-b", "Terminal operation"},
                {"gs-q2-c", "Constructor"}});
        createQuiz("gold-stream", "gs-q3",
            "Does processing a stream modify the original collection?",
            "gs-q3-b", new String[][]{
                {"gs-q3-a", "Yes"},
                {"gs-q3-b", "No"},
                {"gs-q3-c", "Only with map()"}});

        // Lambda quizzes
        createQuiz("gold-lambda", "gl-q1",
            "What symbol separates lambda parameters from the body?",
            "gl-q1-b", new String[][]{
                {"gl-q1-a", "=>"},
                {"gl-q1-b", "->"},
                {"gl-q1-c", "::"}});
        createQuiz("gold-lambda", "gl-q2",
            "What is a functional interface?",
            "gl-q2-c", new String[][]{
                {"gl-q2-a", "An interface with many methods"},
                {"gl-q2-b", "An interface with no methods"},
                {"gl-q2-c", "An interface with exactly one abstract method"}});
        createQuiz("gold-lambda", "gl-q3",
            "Which of these is a valid lambda expression?",
            "gl-q3-a", new String[][]{
                {"gl-q3-a", "x -> x * 2"},
                {"gl-q3-b", "x => x * 2"},
                {"gl-q3-c", "lambda x: x * 2"}});

        // Optional quizzes
        createQuiz("gold-optional", "go-q1",
            "What is Optional designed to replace?",
            "go-q1-a", new String[][]{
                {"go-q1-a", "Returning null from methods"},
                {"go-q1-b", "Exception handling"},
                {"go-q1-c", "Generic types"}});
        createQuiz("gold-optional", "go-q2",
            "What does orElse() do?",
            "go-q2-b", new String[][]{
                {"go-q2-a", "Throws an exception if empty"},
                {"go-q2-b", "Provides a default value if empty"},
                {"go-q2-c", "Returns null if empty"}});
        createQuiz("gold-optional", "go-q3",
            "How do you create an Optional that might be null?",
            "go-q3-c", new String[][]{
                {"go-q3-a", "Optional.of(value)"},
                {"go-q3-b", "Optional.empty()"},
                {"go-q3-c", "Optional.ofNullable(value)"}});

        // Record quizzes
        createQuiz("gold-record", "gr-q1",
            "What does a record automatically generate?",
            "gr-q1-a", new String[][]{
                {"gr-q1-a", "Constructor, accessors, equals, hashCode, toString"},
                {"gr-q1-b", "Only a constructor"},
                {"gr-q1-c", "Only toString"}});
        createQuiz("gold-record", "gr-q2",
            "Are record fields mutable or immutable?",
            "gr-q2-b", new String[][]{
                {"gr-q2-a", "Mutable"},
                {"gr-q2-b", "Immutable"},
                {"gr-q2-c", "Configurable"}});
        createQuiz("gold-record", "gr-q3",
            "How do you access a record component named 'name'?",
            "gr-q3-b", new String[][]{
                {"gr-q3-a", "record.getName()"},
                {"gr-q3-b", "record.name()"},
                {"gr-q3-c", "record.name"}});
    }

    // ========== SPRING MASTER CONTENT ==========

    private void seedSpringMasterContent() {
        createContent("spring-controller",
            "A Controller in Spring is a class annotated with @RestController or @Controller that handles incoming HTTP requests. It maps URLs to methods that process requests and return responses.",
            "Controllers are the entry point for web applications. They connect the outside world (HTTP) to your application logic, defining your API and handling client communication.",
            "Annotate a class with @RestController and use @GetMapping, @PostMapping, etc. to map HTTP methods to handler methods. Return objects that Spring serializes to JSON.",
            "REST Controller",
            "@RestController\n@RequestMapping(\"/api/users\")\npublic class UserController {\n\n    @GetMapping(\"/{id}\")\n    public User getUser(@PathVariable Long id) {\n        return userService.findById(id);\n    }\n}");

        createContent("spring-service",
            "A Service in Spring is a class annotated with @Service that contains business logic. It sits between controllers and repositories, orchestrating operations and enforcing rules.",
            "Services separate business logic from HTTP handling and data access. This makes your code testable, reusable, and maintainable because each layer has a single responsibility.",
            "Annotate your class with @Service. Inject dependencies through the constructor. Implement business methods that controllers can call.",
            "Service Layer",
            "@Service\npublic class UserService {\n    private final UserRepository repository;\n\n    public UserService(UserRepository repository) {\n        this.repository = repository;\n    }\n\n    public User findById(Long id) {\n        return repository.findById(id)\n            .orElseThrow(() -> new NotFoundException(\"User not found\"));\n    }\n}");

        createContent("spring-repository",
            "A Repository in Spring Data JPA is an interface that provides database operations without writing SQL. By extending JpaRepository, you get CRUD methods and can define custom queries.",
            "Repositories abstract away database complexity. You declare what data you need through method names, and Spring generates the implementation, eliminating boilerplate SQL code.",
            "Create an interface extending JpaRepository with entity type and ID type as parameters. Define custom finder methods following Spring naming conventions.",
            "Repository Interface",
            "@Repository\npublic interface UserRepository extends JpaRepository<User, Long> {\n    Optional<User> findByEmail(String email);\n    List<User> findByAgeGreaterThan(int age);\n}");

        createContent("spring-bean",
            "A Bean is an object managed by the Spring IoC container. Spring creates, configures, and manages the lifecycle of beans. They are typically singletons shared across the application.",
            "Beans are the backbone of Spring's dependency injection. By letting Spring manage objects, you get automatic wiring, lifecycle management, and centralized configuration.",
            "Mark classes with @Component, @Service, @Repository, or @Controller to make them beans. Use @Bean in @Configuration classes for manual bean definitions.",
            "Bean Configuration",
            "@Configuration\npublic class AppConfig {\n\n    @Bean\n    public ObjectMapper objectMapper() {\n        return new ObjectMapper()\n            .registerModule(new JavaTimeModule());\n    }\n}");

        createContent("spring-di",
            "Dependency Injection is a pattern where objects receive their dependencies from external sources rather than creating them internally. Spring automates this through its IoC container.",
            "DI makes code loosely coupled and highly testable. Classes declare what they need without knowing where it comes from, making it easy to swap implementations or mock for testing.",
            "Use constructor injection (preferred) by declaring dependencies as constructor parameters. Spring automatically resolves and injects the matching beans.",
            "Constructor Injection",
            "@Service\npublic class OrderService {\n    private final PaymentService payments;\n    private final InventoryService inventory;\n\n    public OrderService(PaymentService payments,\n                        InventoryService inventory) {\n        this.payments = payments;\n        this.inventory = inventory;\n    }\n}");
    }

    // ========== SPRING MASTER QUIZZES ==========

    private void seedSpringMasterQuizzes() {
        // Controller quizzes
        createQuiz("spring-controller", "spc-q1",
            "Which annotation marks a class as a REST controller?",
            "spc-q1-a", new String[][]{
                {"spc-q1-a", "@RestController"},
                {"spc-q1-b", "@Controller"},
                {"spc-q1-c", "@Service"},
                {"spc-q1-d", "@Component"}});
        createQuiz("spring-controller", "spc-q2",
            "Which annotation maps a GET request to a method?",
            "spc-q2-b", new String[][]{
                {"spc-q2-a", "@RequestMapping"},
                {"spc-q2-b", "@GetMapping"},
                {"spc-q2-c", "@Get"}});
        createQuiz("spring-controller", "spc-q3",
            "What does @PathVariable do?",
            "spc-q3-c", new String[][]{
                {"spc-q3-a", "Defines a query parameter"},
                {"spc-q3-b", "Sets the response type"},
                {"spc-q3-c", "Extracts a value from the URL path"}});

        // Service quizzes
        createQuiz("spring-service", "sps-q1",
            "What is the primary purpose of a @Service class?",
            "sps-q1-b", new String[][]{
                {"sps-q1-a", "Handle HTTP requests"},
                {"sps-q1-b", "Contain business logic"},
                {"sps-q1-c", "Access the database directly"}});
        createQuiz("spring-service", "sps-q2",
            "Where does a Service sit in the layered architecture?",
            "sps-q2-a", new String[][]{
                {"sps-q2-a", "Between Controller and Repository"},
                {"sps-q2-b", "Above the Controller"},
                {"sps-q2-c", "Inside the Repository"}});
        createQuiz("spring-service", "sps-q3",
            "How should a Service receive its dependencies?",
            "sps-q3-c", new String[][]{
                {"sps-q3-a", "Using new keyword"},
                {"sps-q3-b", "Through static methods"},
                {"sps-q3-c", "Through constructor injection"}});

        // Repository quizzes
        createQuiz("spring-repository", "spr-q1",
            "Which interface should a Spring Data JPA repository extend?",
            "spr-q1-b", new String[][]{
                {"spr-q1-a", "CrudRepository"},
                {"spr-q1-b", "JpaRepository"},
                {"spr-q1-c", "Repository"}});
        createQuiz("spring-repository", "spr-q2",
            "Do you need to write SQL for basic CRUD operations with Spring Data JPA?",
            "spr-q2-b", new String[][]{
                {"spr-q2-a", "Yes, always"},
                {"spr-q2-b", "No, they are auto-generated"},
                {"spr-q2-c", "Only for reads"}});
        createQuiz("spring-repository", "spr-q3",
            "How does Spring create query implementations from method names?",
            "spr-q3-a", new String[][]{
                {"spr-q3-a", "By parsing method name conventions like findByEmail"},
                {"spr-q3-b", "By reading comments above the method"},
                {"spr-q3-c", "By requiring @Query on every method"}});

        // Bean quizzes
        createQuiz("spring-bean", "spb-q1",
            "What is the default scope of a Spring bean?",
            "spb-q1-a", new String[][]{
                {"spb-q1-a", "Singleton"},
                {"spb-q1-b", "Prototype"},
                {"spb-q1-c", "Request"},
                {"spb-q1-d", "Session"}});
        createQuiz("spring-bean", "spb-q2",
            "Which annotation explicitly defines a bean in a configuration class?",
            "spb-q2-c", new String[][]{
                {"spb-q2-a", "@Component"},
                {"spb-q2-b", "@Autowired"},
                {"spb-q2-c", "@Bean"}});
        createQuiz("spring-bean", "spb-q3",
            "Who manages the lifecycle of a Spring bean?",
            "spb-q3-b", new String[][]{
                {"spb-q3-a", "The developer explicitly"},
                {"spb-q3-b", "The Spring IoC container"},
                {"spb-q3-c", "The JVM garbage collector"}});

        // Dependency Injection quizzes
        createQuiz("spring-di", "spdi-q1",
            "What is the recommended way to inject dependencies in Spring?",
            "spdi-q1-c", new String[][]{
                {"spdi-q1-a", "Field injection with @Autowired"},
                {"spdi-q1-b", "Setter injection"},
                {"spdi-q1-c", "Constructor injection"}});
        createQuiz("spring-di", "spdi-q2",
            "What problem does Dependency Injection solve?",
            "spdi-q2-a", new String[][]{
                {"spdi-q2-a", "Tight coupling between classes"},
                {"spdi-q2-b", "Slow performance"},
                {"spdi-q2-c", "Memory leaks"}});
        createQuiz("spring-di", "spdi-q3",
            "What does IoC stand for?",
            "spdi-q3-b", new String[][]{
                {"spdi-q3-a", "Input/Output Control"},
                {"spdi-q3-b", "Inversion of Control"},
                {"spdi-q3-c", "Instance of Class"}});
    }
}
