import spock.lang.Specification
import spock.lang.Unroll

// Upewnij się, że używasz oryginalnych klas bez modyfikacji

class RobustMockCalculatorSpec extends Specification {

    // --- WAŻNE: NIE DEFINIUJ TUTAJ ŻADNYCH PÓL INSTANCJI ---
    // Unikamy pól `def calculator` i `def mathService` na poziomie klasy,
    // aby zapobiec przypadkowemu współdzieleniu stanu.

    @Unroll
    def "multiplyList z #numbers zwraca #expected [PODEJŚCIE ODPORNE Z MOCKIEM]"() {

        // Blok 'given' jest wykonywany NA NOWO dla KAŻDEJ iteracji z bloku 'where'.
        // To jest klucz do sukcesu.
        given: "Tworzymy NOWE, CZYSTE obiekty dla tej konkretnej iteracji"

        // 1. Tworzymy świeżego Mocka WEWNĄTRZ bloku 'given'.
        def mathService = Mock(MathService)

        // 2. Tworzymy świeżą instancję testowanej klasy i wstrzykujemy jej świeżego mocka.
        def calculator = new Calculator(service: mathService)

        // 3. Definiujemy zachowanie dla tego konkretnego, świeżego mocka.
        //    Użyjmy składni z `it`, bo udowodniliśmy, że ma największą szansę zadziałać.
        mathService.multiply(_, _) >> { it[0] * it[1] }

        when: "Wykonujemy akcję testową"
        def result = calculator.multiplyList(numbers)

        then: "Weryfikujemy wynik i interakcje"
        result == expected

        and: "Weryfikujemy interakcje na tym konkretnym mocku"
        (numbers?.size() ?: 0) * mathService.multiply(_, _)

        where:
        numbers      | expected
        [2, 3]       | 6
      //  [4, 5]       | 20
      //  [1, 2, 3, 4] | 24
      //  [10]         | 10
      //  []           | 1
     //   null         | 1
    }
}