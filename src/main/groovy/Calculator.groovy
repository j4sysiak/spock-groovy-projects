/*
 * Plik: CalculatorSpec.groovy
 * Opis: Prosty tutorial testowania z użyciem Spock + Groovy + wersje środowiska
 */

class Calculator {

    MathService service

    int multiplyList(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) return 1
        int result = 1
        //numbers.each { n -> result = safeMultiply(result, n) }
        for (Integer n : numbers) {
            result = safeMultiply(result, n)
        }
        return result
    }

    private int safeMultiply(int a, int b) {
        // Dodajemy warunek, którego oczekuje test:
        // Jeśli którykolwiek z argumentów jest zerem, idź na skróty.
        if (a == 0 || b == 0) {
            return 0 // Zwróć 0 i NIE wywołuj serwisu
        }

        // Wywołaj serwis tylko wtedy, gdy żaden z argumentów nie jest zerem.
        // Ta metoda używa serwisu, więc musimy ją zamockować
        // jeśli nie chcemy, aby była wywoływana
        return service.multiply(a, b)   // to jest mock tego wywolania:  mathService.multiply(_, _) >> { int a, int b -> a * b }
    }

    int add(int a, int b) {
        a + b
    }

    int subtract(int a, int b) {
        a - b
    }

    int multiply(int a, int b) {
        a * b
    }

    int divide(int a, int b) {
        if (b == 0) throw new ArithmeticException("Cannot divide by zero")
        a / b
    }
}