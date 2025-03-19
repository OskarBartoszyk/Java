import java.util.ArrayList;
import java.util.function.Supplier;

// Definicja interfejsu funkcyjnego
@FunctionalInterface
interface AddTwo {
    int add(int value);
}

public class Test {
    public static void main(String[] args) {
        ArrayList<Integer> addition = new ArrayList<>();
        addition.add(1);
        addition.add(2);

        // Lambda implementująca metodę 'add'
        AddTwo addintwo = (int value) -> value + 2;

        // Przetwarzamy listę i wypisujemy zmodyfikowane wartości
        addition.forEach((number) -> System.out.println(addintwo.add(number)));

        // Wypisujemy oryginalne wartości (bez modyfikacji)
        addition.forEach(System.out::println);
        
    }
}
