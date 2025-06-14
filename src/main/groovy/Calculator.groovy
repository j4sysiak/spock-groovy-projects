/*
 * Plik: CalculatorSpec.groovy
 * Opis: Prosty tutorial testowania z użyciem Spock + Groovy + wersje środowiska
 */

class Calculator {

    MathService service

    int safeMultiply(int a, int b) {
        if (a == 0 || b == 0) return 0
        return service.multiply(a, b)
    }

    int multiplyList(List<Integer> numbers) {
        int result = 1
        // Poprawiona logika, aby faktycznie mnożyć elementy listy
        numbers.each { n -> result = safeMultiply(result, n) }
        return result
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