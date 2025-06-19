import spock.lang.Specification

// Ten test nie potrzebuje żadnych innych klas
class MockSanityCheckSpec extends Specification {

    def "czy mock poprawnie zwraca zdefiniowaną wartość"() {
        given: "Tworzymy prostego mocka interfejsu MathService"
        def isolatedMock = Mock(MathService)

        and: "Definiujemy, że dla dowolnych argumentów ma zwrócić konkretną wartość"
        isolatedMock.multiply(_, _) >> 999 // Zwróć 999, żeby było jasne

        when: "Wywołujemy na nim metodę"
        def value = isolatedMock.multiply(5, 10) // Wywołujemy z dowolnymi liczbami

        then: "Sprawdzamy, czy zwrócił to, co kazaliśmy"
        value == 999 // Czy wynik to 999?
    }
}