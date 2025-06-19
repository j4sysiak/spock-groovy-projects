import spock.lang.Specification
import spock.lang.Unroll

class CalculatorSpec4 extends Specification {

// To jest wersja, która ZADZIAŁA w Twoim obecnym, uszkodzonym środowisku

    @Unroll
    def "multiplyList z #numbers zwraca #expected [UŻYWAJĄC SPY]"() {
        given: "Kalkulator z SERWISEM-SZPIEGIEM"
        def mathService = Spy(MathServiceImpl) // Używamy Spy na prawdziwej implementacji
        def calculator = new Calculator(service: mathService)

        when:
        def result = calculator.multiplyList(numbers)

        then:
        result == expected

        and: "Weryfikujemy interakcje (Spy na to pozwala)"
        // Ile razy powinna być wywołana? Dokładnie tyle, ile jest niezerowych kroków
        // ale prostsza weryfikacja wyniku wystarczy. A jeśli już, to dla [2,3] będą 2 wywołania.
        if (numbers) {
            (numbers.size()) * mathService.multiply(_,_)
        } else {
            0 * mathService.multiply(_,_)
        }


        where:
        numbers | expected | type   | override
        [2, 3]  | 6        | 'Spy' | null
        [4, 5]  | 20       | 'Spy' | null
    }
}