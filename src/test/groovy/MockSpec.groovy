import spock.lang.Specification
import spock.lang.Unroll

class MockSpec extends Specification {

    /*
    - Używa Mock()
    - Asercje liczby wywołań (calls * multiply(_, _))
    - Parametryzacja z @Unroll
    */

    @Unroll
    def "Mock: multiplyList #numbers returns #expected"() {
        given:
        def service = Mock(MathService)
        def calc = new Calculator(service: service)

        calls * service.multiply(_, _) >> { int a, int b -> a * b }

        when:
        def result = calc.multiplyList(numbers)

        then:
        result == expected

        where:
        numbers     | expected | calls
        [2, 3]      | 6        | 2
        [1, 0, 9]   | 0        | 1
        [5]         | 5        | 1
    }
}