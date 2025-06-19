import spock.lang.Specification

class CalculatorSpec3 extends Specification {

    // Testowany obiekt
    Calculator calculator
    // Zależność, którą będziemy mockować/stubować/szpiegować
    MathService mathService

    // --- POPRAWKA: Użycie setup() do przygotowania testu ---
    // 'setup' wykonuje się przed każdą metodą testową (każdym 'where' blockiem)
    def setup() {
        // Tworzymy świeże obiekty dla każdego testu, aby uniknąć interferencji
        mathService = Mock(MathService) // Domyślnie używamy Mocka
        calculator = new Calculator(service: mathService)
    }

    def "powinien poprawnie pomnożyć listę liczb używając Mocka"() {
        given: "Lista liczb do pomnożenia"
        def numbers = [2, 3]

        and: "Definiujemy zachowanie Mocka, aby zwracał prawdziwy wynik mnożenia"
        // To jest kluczowy element. Mock musi wiedzieć, co zwrócić.
        // Jeśli tego nie ma, zwraca 0, a mnożenie przez 0 psuje dalsze obliczenia.
        mathService.multiply(_, _) >> { int a, int b -> a * b }

        when: "Wywołujemy metodę"
        def result = calculator.multiplyList(numbers)

        then: "Wynik jest poprawny"
        result == 6

        and: "Metoda multiply została wywołana odpowiednią liczbę razy"
        // 1. wywołanie: service.multiply(1, 2)
        // 2. wywołanie: service.multiply(2, 3)
        2 * mathService.multiply(_, _)
    }

    // --- Pozostałe testy dla kompletności ---

    def "powinien poprawnie pomnożyć listę używając Stuba"() {
        given:
        // Nadpisujemy domyślny Mock ze 'setup'
        mathService = Stub(MathService)
        calculator.service = mathService // Aktualizujemy serwis w kalkulatorze

        and: "Definiujemy zachowanie Stuba"
        mathService.multiply(_, _) >> { int a, int b -> a * b }

        when:
        def result = calculator.multiplyList([4, 5])

        then:
        result == 20
        // Dla Stuba nie weryfikujemy interakcji
    }

    def "powinien poprawnie pomnożyć listę używając Spya (implementacja rzeczywista)"() {
        given:
        // Używamy Spya na prawdziwej implementacji
        mathService = Spy(MathServiceImpl)
        calculator.service = mathService

        when:
        def result = calculator.multiplyList([3, 4])

        then:
        result == 12
        2 * mathService.multiply(_, _) // Weryfikujemy, że prawdziwa metoda została wywołana
    }

    def "powinien użyć nadpisanej wartości dla Spya"() {
        given:
        mathService = Spy(MathServiceImpl)
        calculator.service = mathService

        and: "Nadpisujemy zachowanie dla konkretnego wywołania"
        // Mnożenie 1*2 użyje prawdziwej implementacji, ale 2*3 zwróci 999
        mathService.multiply(2, 3) >> 999

        when:
        def result = calculator.multiplyList([2, 3])

        then: "Wynik końcowy jest zgodny z nadpisaniem"
        result == 999
    }
}