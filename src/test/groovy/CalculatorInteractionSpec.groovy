import spock.lang.Specification
import org.spockframework.mock.runtime.GroovyMockFactory // Importujemy potrzebną klasę

class CalculatorInteractionSpec extends Specification {

    def "sprawdzamy, czy kalkulator FAKTYCZNIE używa naszego mocka"() {
        given:
        def mathServiceMock = Mock(MathService)
        def calculator = new Calculator(service: mathServiceMock)

        // --- LINIE DIAGNOSTYCZNE (POPRAWIONE) ---
        println "Klasa serwisu wewnątrz kalkulatora: " + calculator.service.getClass().getName()

        // Używamy pełnej nazwy, aby sprawdzić, czy obiekt jest mockiem Spocka
        // Jeśli ta linia rzuci błąd, to znaczy, że nie jest to mock!
        def isMock = GroovyMockFactory.isMock(calculator.service)
        println "Czy serwis w kalkulatorze jest mockiem? " + isMock


        // Definiujemy zachowanie
        mathServiceMock.multiply(_, _) >> { a, b -> a * b }

        when:
        def result = calculator.multiplyList([2, 3])

        then:
        // Na razie wynik nie jest ważny. Patrzymy na wydruk w konsoli.
        result == 6
    }
}