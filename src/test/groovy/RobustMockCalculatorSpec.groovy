import spock.lang.Specification
import spock.lang.Unroll

// Załóżmy, że te klasy są zdefiniowane gdzieś indziej
// interface MathService { int multiply(int a, int b) }
// class Calculator { ... }

class RobustMockCalculatorSpec extends Specification {

    @Unroll
    def "multiplyList z #numbers zwraca #expected i wywołuje serwis #calls razy"() {

        given: "Tworzymy NOWE, CZYSTE obiekty dla tej konkretnej iteracji"
        // 1. Tworzymy świeżego Mocka.
        def mathService = Mock(MathService)
        // 2. Tworzymy świeżą instancję testowanej klasy.
        def calculator = new Calculator(service: mathService)
        // 3. UWAGA: NIE definiujemy tutaj zachowania mocka! Robimy to w bloku 'then:'.

        when: "Wykonujemy akcję testową"
        def result = calculator.multiplyList(numbers)

        then: "Weryfikujemy wynik i interakcje w JEDNYM miejscu"
        // Ta linia łączy weryfikację krotności (calls *) ze stubowaniem (>> { ... }).
        // Spock jest na tyle sprytny, że część stubującą "instaluje" przed blokiem 'when',
        // a część weryfikującą sprawdza po bloku 'when'.
        calls * mathService.multiply(_, _) >> { it[0] * it[1] }

        and: "Weryfikujemy ostateczny wynik"
        result == expected

        where: "Dostarczamy kompletne dane testowe"
        // Musimy dodać kolumnę 'calls', aby test wiedział, ile wywołań oczekiwać.
        numbers      | expected | calls
        [2, 3]       | 6        | 2       // safeMultiply(1,2) i safeMultiply(2,3) -> 2 wywołania
        [5]          | 5        | 1       // safeMultiply(1,5) -> 1 wywołanie
        [2, 3, 4]    | 24       | 3       // 3 wywołania
        []           | 1        | 0       // 0 wywołań
        [10, 0, 20]  | 0        | 1       // safeMultiply(1,10), potem safeMultiply(10,0) zwraca 0 bez wywołania serwisu
    }
}