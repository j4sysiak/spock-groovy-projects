import spock.lang.Specification
import spock.lang.Unroll

class StubSpec extends Specification {

    /*
    Używa Stub()
    Bez weryfikacji interakcji
    Skupia się na poprawnym wyniku (result == expected)
    */

    @Unroll
    def "Stub: multiplyList #numbers returns #expected"() {
        given:
        def service = Stub(MathService)
        def calc = new Calculator(service: service)

        service.multiply(_, _) >> { int a, int b -> a * b }

        when:
        def result = calc.multiplyList(numbers)

        then:
        result == expected

        where:
        numbers     | expected
        [2, 3]      | 6
        [4, 5]      | 20
        []          | 1
    }
}