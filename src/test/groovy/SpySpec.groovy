import spock.lang.Specification
import spock.lang.Unroll

        /*
        Używa Spy(MathServiceImpl)
        Loguje multiply: a * b
        Warunkowo nadpisuje (@Override) multiply(a, b) przez kolumnę override
        */

class SpySpec extends Specification {

    @Unroll
    def "Spy: multiplyList #numbers returns #expected with optional override #override"() {
        given:
        def realImpl = new MathServiceImpl() {
            @Override
            int multiply(int a, int b) {
                println "multiply: $a * $b"
                return a * b
            }
        }

        def service = Spy(realImpl)
        def calc = new Calculator(service: service)

        when:
        def result = calc.multiplyList(numbers)

        then:
        if (override != null) {
            1 * service.multiply(override.a, override.b) >> override.result
        }

        and:
        result == expected

        where:
        numbers     | expected | override
        [2, 3]      | 999      | [a: 2, b: 3, result: 999]
        [3, 4]      | 12       | null
        [1, 0, 5]   | 0        | null
    }
}