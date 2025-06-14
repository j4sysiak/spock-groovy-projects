/*
 * Plik: CalculatorSpec.groovy
 * Opis: Prosty tutorial testowania z użyciem Spock + Groovy + wersje środowiska
 */

class Calculator {
    int add(int a, int b) { a + b }
    int subtract(int a, int b) { a - b }
    int multiply(int a, int b) { a * b }
    int divide(int a, int b) {
        if (b == 0) throw new ArithmeticException("Cannot divide by zero")
        a / b
    }
}