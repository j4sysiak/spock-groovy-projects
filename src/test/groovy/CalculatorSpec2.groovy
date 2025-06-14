import spock.lang.*

class CalculatorSpec2 extends Specification {
    def calculator = new Calculator()

    def "should add numbers"() {
        expect:
        calculator.add(2, 3) == 5
    }
}
