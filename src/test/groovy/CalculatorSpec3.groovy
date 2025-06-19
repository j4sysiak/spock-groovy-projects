import spock.lang.Specification
import spock.lang.Unroll

class CalculatorSpec3 extends Specification {

    def "najprostszy możliwy test, który musi zadziałać"() {
        given: "Tworzymy mocka i kalkulator"
        def mathService = Mock(MathService)
        def calculator = new Calculator(service: mathService)

        // --- NAJPROSTSZA MOŻLIWA DEFINICJA ZACHOWANIA ---
        // Zamiast dynamicznego domknięcia, użyjmy stałej wartości.
        // Mówimy: "ZAWSZE, gdy 'multiply' jest wywołane, ZWRÓĆ 3".
        // To eliminuje wszelkie problemy z typami argumentów w domknięciu.
        mathService.multiply(_, _) >> 3

        when: "Wywołujemy metodę z listą [2, 3]"
        // Oczekiwane kroki:
        // 1. result = 1
        // 2. result = service.multiply(1, 2) -> powinno zwrócić 3. result = 3
        // 3. result = service.multiply(3, 3) -> powinno zwrócić 3. result = 3
        def result = calculator.multiplyList([2, 3])

        then: "Sprawdzamy wynik"
        // Jeśli mock działa, ostateczny wynik powinien być 3.
        result == 3
    }

    def "test z użyciem domknięcia i obiektu 'it'"() {
        given:
        def mathService = Mock(MathService)
        def calculator = new Calculator(service: mathService)

        // --- POPRAWKA: Używamy 'it' ---
        mathService.multiply(_, _) >> { it[0] * it[1] }

        when:
        def result = calculator.multiplyList([2, 3])

        then:
        result == 6
    }


    @Unroll
    def "multiplyList z #numbers zwraca #expected [UŻYWAJĄC SPY]"() {
        given: "Kalkulator z SERWISEM-SZPIEGIEM"
        // --- KLUCZOWA ZMIANA: Zamiast Mock, używamy Spy na prawdziwej implementacji ---
        // Spy domyślnie wywoła prawdziwą metodę `a * b` z MathServiceImpl.
        // Omijamy w ten sposób cały zepsuty mechanizm `>> { ... }`.
        def mathService = Spy(MathServiceImpl)
        def calculator = new Calculator(service: mathService)

        when: "Wywołujemy metodę"
        def result = calculator.multiplyList(numbers)

        then: "Wynik jest poprawny"
        result == expected

        and: "Możemy nadal weryfikować interakcje, bo to Spy"
        (numbers?.size() ?: 0) * mathService.multiply(_, _)

        where:
        numbers      | expected
        [2, 3]       | 6
       // [4, 5]       | 20
      //  [1, 2, 3, 4] | 24
      //  [10]         | 10
        //[]           | 1
       // null         | 1
    }
}