import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class CalculatorSpec extends Specification {

    @Shared
    def sharedCalculator = new Calculator()

    def calculator

    def setupSpec() {
        println "==> SETUP SPEC (once per class)"
    }

    def cleanupSpec() {
        println "==> CLEANUP SPEC (once per class)"
    }

    def setup() {
        println "--> SETUP (before each test)"
        calculator = new Calculator()
    }

    def cleanup() {
        println "<-- CLEANUP (after each test)"
    }

    def "save system environment versions to file"() {
        when:
        def spockVer = spock.lang.Specification.package.implementationVersion
        def groovyVer = GroovySystem.version
        def javaVer = System.getProperty("java.version")

        def output = """
        Spock version: $spockVer
        Groovy version: $groovyVer
        Java version: $javaVer
        """.stripIndent().trim()

        new File("build/system-info.txt").text = output

        then:
        noExceptionThrown()
    }

    def "should add two numbers"() {
        expect:
        calculator.add(2, 3) == 5
    }

    def "should subtract two numbers"() {
        expect:
        calculator.subtract(5, 3) == 2
    }

    def "should multiply two numbers"() {
        expect:
        calculator.multiply(4, 3) == 12
    }

    def "should divide two numbers"() {
        expect:
        calculator.divide(10, 2) == 5
    }

    def "should throw when dividing by zero"() {
        when:
        calculator.divide(10, 0)

        then:
        def e = thrown(ArithmeticException)
        e.message == "Cannot divide by zero"
    }

    def "demonstrate shared object and lifecycle hooks"() {
        expect:
        sharedCalculator.multiply(2, 5) == 10
    }

    @Unroll
    def "should add multiple pairs: #a + #b = #result"() {
        expect:
        calculator.add(a, b) == result

        where:
        a | b || result
        1 | 2 || 3
        0 | 0 || 0
        5 | 5 || 10
        -3 | 3 || 0
    }

    @Unroll
    def "should subtract multiple pairs: #a - #b = #result"() {
        expect:
        calculator.subtract(a, b) == result

        where:
        a | b || result
        1 | 2 || -1
        0 | 0 || 0
        5 | 5 || 0
        -3 | 3 || -6
    }

    @Unroll
    def "mocked multiply: #a * #b = #result"() {
        given:
        def service = Mock(MathService)
        service.multiply(a, b) >> result
        def calc = new Calculator(service: service)

        expect:
        calc.safeMultiply(a, b) == result

        where:
        a | b || result
        2 | 3 || 6
        0 | 5 || 0
        4 | 1 || 4
    }



    def "should skip multiply call when zero involved"() {
        given:
        def service = Mock(MathService)
        def calc = new Calculator(service: service)

        when:
        def result = calc.safeMultiply(0, 10)

        then:
        result == 0
        0 * service._  // no methods on service should be called
    }

// Poprawiony test
    def "should multiply all numbers in the list"() {
        given:
        def service = Mock(MathService)
        def calc = new Calculator(service: service)

        when:
        def result = calc.multiplyList([2, 5])

        then:
        // 1. Pierwsze wywołanie: result=1, n=2 -> service.multiply(1, 2)
        //    Stubbing: niech zwróci 2 (1*2)
        1 * service.multiply(1, 2) >> 2

        // 2. Drugie wywołanie: result=2, n=5 -> service.multiply(2, 5)
        //    Stubbing: niech zwróci 10 (2*5)
        1 * service.multiply(2, 5) >> 10

        result == 10
    }

    @Unroll
    def "should multiply all elements of #numbers to get #expected"() {
        given:
        def service = Mock(MathService) {
            multiply(_, _) >> { args -> args[0] * args[1] }
        }
        def calc = new Calculator(service: service)

        when:
        def result = calc.multiplyList(numbers)

        then:
        result == expected

        where:
        numbers        || expected
        [2, 5]         || 10
        [1, 1, 1]      || 1
        [3, 3, 3]      || 27
        [7]            || 7
        []             || 1
        [2, 0, 5]      || 0
    }


    @Unroll
    def "multiplyList of #numbers gives #expected and calls multiply() #calls time(s)"() {
        given: "a mock service and a calculator"
        // W bloku `given` TYLKO tworzymy obiekty.
        // NIE definiujemy tutaj zachowania mocka.
        MathService service = Mock()
        def calc = new Calculator(service: service)

        when: "multiplyList is called"
        def result = calc.multiplyList(numbers)

        then: "the result is correct and the service was called the expected number of times"
        // W bloku `then` łączymy weryfikację liczby wywołań ze stubowaniem odpowiedzi.
        // To jest kluczowa zmiana!
        calls * service.multiply(_, _) >> { int a, int b -> a * b }

        and: "the final result is as expected"
        result == expected

        where: "various inputs are tested"
        numbers        | expected | calls
        [2, 5]         | 10       | 2
        [1, 2, 3]      | 6        | 3
        [7]            | 7        | 1
        []             | 1        | 0
        [3, 0, 5]      | 0        | 1
        [10, 20]       | 200      | 2
    }

    @Unroll
    def "multiplyList of #numbers gives #expected (Stub version)"() {
        given: "a stubbed MathService and a Calculator"
        MathService service = Stub()
        service.multiply(_, _) >> { int a, int b -> a * b }
        def calc = new Calculator(service: service)

        expect: "result matches expected outcome"
        calc.multiplyList(numbers) == expected

        where:
        numbers        | expected
        [2, 5]         | 10
        [1, 2, 3]      | 6
        [7]            | 7
        []             | 1
        [3, 0, 5]      | 0
        [10, 20]       | 200
    }

    @Unroll
    def "multiplyList1 of #numbers gives #expected (Stub version)"() {
        given:
        def service = Stub(MathService)
        service.multiply(_, _) >> { a, b -> a * b }
        def calc = new Calculator(service: service)

        expect:
        calc.multiplyList(numbers) == expected

        where:
        numbers     | expected
        [2, 3]      | 6
        [1, 1, 1]   | 1
        [5]         | 5
    }

    @Unroll
    def "multiplyList of #numbers gives #expected using Stub"() {
        given:
        def service = Stub(MathService)
        service.multiply(_, _) >> { a, b -> a * b }
        def calc = new Calculator(service: service)

        expect:
        calc.multiplyList(numbers) == expected

        where:
        numbers         | expected
        [2, 3]          | 6       // 1*2=2, 2*3=6
        [1, 2, 3, 4]    | 24      // 1*1=1 → *2=2 → *3=6 → *4=24
        [10]            | 10
        []              | 1       // neutralny element mnożenia
        [3, 0, 9]       | 0       // zera zawsze zerują
        [5, 5]          | 25
    }

    @Unroll
    def "stubbed multiply (no interaction check): #a * #b = #expected"() {
        given:
        def service = Stub(MathService) {
            multiply(_, _) >> { args -> args[0] * args[1] }
        }
        def calc = new Calculator(service: service)

        expect:
        calc.safeMultiply(a, b) == expected

        where:
        a | b || expected
        1 | 1 || 1
        5 | 0 || 0
        3 | 7 || 21
    }

    @Unroll
    def "multiplyList #numbers zwraca #expected i wywołuje multiply() dokładnie #calls razy"() {
        given:
        def service = Mock(MathService)
        def calc = new Calculator(service: service)

        when:
        def result = calc.multiplyList(numbers)

        then:
        if (calls > 0) {
            calls * service.multiply(_, _) >> { int a, int b -> a * b }
        } else {
            0 * service.multiply(_, _)
        }

        result == expected

        where:
        numbers         | expected | calls
        [2, 3]          | 6        | 2
        [1, 2, 3, 4]    | 24       | 4
        [10]            | 10       | 1
        []              | 1        | 0
        [3, 0, 9]       | 0        | 1
        [5, 5]          | 25       | 2
    }



}