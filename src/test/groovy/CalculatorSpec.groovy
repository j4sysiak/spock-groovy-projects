import spock.lang.IgnoreIf
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

        then: "sprawdzenie liczby i zachowania wywołań"
        if (calls > 0) {
            calls * service.multiply(_, _) >> { int a, int b -> a * b }
        } else {
            0 * service.multiply(_, _)
        }

        and: "oczekiwany wynik końcowy"
        result == expected

        where:
        numbers         | expected | calls
        [2, 3]          | 6        | 2
        /*[1, 2, 3, 4]    | 24       | 4
        [10]            | 10       | 1
        []              | 1        | 0
        [3, 0, 9]       | 0        | 1
        [5, 5]          | 25       | 2*/
    }

    @Unroll
    def "multiplyList #numbers zwraca #expected, gdzie Spy nadpisuje tylko multiply(2, 3)"() {
        given: "rzeczywista implementacja + Spy"
        def realService = new MathServiceImpl()
        def spyService = Spy(realService)
        def calc = new Calculator(service: spyService)

        when: "uruchamiamy multiplyList(...)"
        def result = calc.multiplyList(numbers)

        then: "jeśli multiply(2, 3), to Spy zwraca 999"
        if (numbers == [2, 3]) {
             1 * spyService.multiply(2, 3) >> 999
        } else {
            // brak asercji interakcji
        }

        and:
        result == expected
        noExceptionThrown()

        where:
        numbers     | expected
        [2, 3]      | 999      // Spy nadpisuje multiply(2, 3)
        [3, 4]      | 12       // działa prawdziwe mnożenie
    }


    @Unroll
    def "multiplyList #numbers zwraca #expected (Spy bez override)"() {
        given:
        def spyService = Spy(MathServiceImpl)
        def calc = new Calculator(service: spyService)

        when:
        def result = calc.multiplyList(numbers)

        then:
        result == expected

        where:
        numbers     | expected
        [2, 3]      | 6        // 1 * 2 * 3 = 6
        [3, 4]      | 12       // 1 * 3 * 4 = 12
        []          | 1
        [1, 0, 7]   | 0
    }

    @Unroll
    def "multiplyList #numbers zwraca #expected, z logowaniem multiply()"() {
        given:
        def realService = new MathServiceImpl() {
            @Override
            int multiply(int a, int b) {
                println "Override multiply: $a * $b"
                return a * b
            }
        }

        def spyService = Spy(realService)
        def calc = new Calculator(service: spyService)

        when:
        def result = calc.multiplyList(numbers)

        then:
        if (override != null) {
            1 * spyService.multiply(override.a, override.b) >> override.result
        }

        and:
        result == expected

        where:
        numbers     | expected | override
        [2, 3]      | 999      | [a: 2, b: 3, result: 999] // nadpisujemy multiply(2, 3)
        [3, 4]      | 12       | null                     // używa realnych multiply
    }

    @Unroll
    def "multiplyList #numbers zwraca #expected dla #type"() {
        given:
        MathService service

        // pełna kontrola nad ilością wywołań (calls * multiply(...))
        if (type == 'Mock') {
            service = Mock(MathService)
            calls * service.multiply(_, _) >> { int a, int b -> a * b }
        }

        // brak weryfikacji wywołań, ale zwraca wynik
        if (type == 'Stub') {
            service = Stub(MathService)
            service.multiply(_, _) >> { int a, int b -> a * b }
        }

        // 	wywołuje prawdziwą metodę (loguje multiply: a * b), ale można nadpisać @Override
        if (type == 'Spy') {
            def realImpl = new MathServiceImpl() {
                @Override
                int multiply(int a, int b) {
                    println "🔢 multiply: $a * $b"
                    return a * b
                }
            }
            service = Spy(realImpl)
        }

        def calc = new Calculator(service: service)

        when:
        def result = calc.multiplyList(numbers)

        then:
        result == expected

        where:
        numbers     | expected | calls | type
        [2, 3]      | 6        | 2     | 'Mock'
        [4, 5]      | 20       | null  | 'Stub'
        [2, 3]      | 6        | null  | 'Spy'
        [1, 0, 9]   | 0        | 1     | 'Mock'
    }

    @Unroll
    def "multiplyList #numbers zwraca #expected dla #type z override: #override"() {
        given:
        MathService service

        if (type == 'Mock') {
            service = Mock(MathService)
            calls * service.multiply(_, _) >> { int a, int b -> a * b }
        }

        if (type == 'Stub') {
            service = Stub(MathService)
            service.multiply(_, _) >> { int a, int b -> a * b }
        }

        if (type == 'Spy') {
            def realImpl = new MathServiceImpl() {
                @Override
                int multiply(int a, int b) {
                    println "🔢 multiply: $a * $b"
                    return a * b
                }
            }
            service = Spy(realImpl)
        }

        def calc = new Calculator(service: service)

        when:
        def result = calc.multiplyList(numbers)

        then:
        if (type == 'Spy' && override != null) {
            1 * service.multiply(override.a, override.b) >> override.result
        }

        and:
        result == expected

        where:
        numbers | expected | calls | type  | override
        // [2, 3]    | 6        | 2     | 'Mock' | null
        // [4, 5]    | 20       | null  | 'Stub' | null
        // [2, 3]    | 999      | null  | 'Spy'  | [a: 2, b: 3, result: 999]
        [3, 4]  | 12       | null  | 'Spy' | null
        // [1, 0, 7] | 0        | 1     | 'Mock' | null

        /*
        Działanie w skrócie:
        type	        override	                     Co się stanie
        Spy	            {a:2, b:3, result:999}	         tylko multiply(2, 3) zwróci 999
        Spy	            null	                         działa prawdziwa metoda multiply()
        Mock	        dowolne	                         działa wg calls * multiply(_, _) >> ...
        Stub	        dowolne	                         działa zawsze multiply(_, _) >> a*b


        type	calls	  Zachowanie
        Mock	liczba	  pełna kontrola nad ilością wywołań (calls * multiply(...))
        Stub	null	  brak weryfikacji wywołań, ale zwraca wynik
        Spy	    null	  wywołuje prawdziwą metodę (loguje multiply: a * b), ale można nadpisać
    }*/
    }


    @Unroll
    def "multiplyList z #numbers zwraca #expected dla #type"() {
        given:
        MathService service
        def realImpl = new MathServiceImpl()

        switch (type) {
            case 'Mock':
                service = Mock(MathService)
                // Konfiguracja Mocka jest kluczowa!
                service.multiply(_, _) >> { int a, int b -> a * b }
                break
            case 'Stub':
                service = Stub(MathService)
                service.multiply(_, _) >> { int a, int b -> a * b }
                break
            case 'Spy':
                service = Spy(realImpl)
                if (override?.result != null) {
                    service.multiply(override.a, override.b) >> override.result
                }
                break
        }
        def calc = new Calculator(service: service)

        when:
        def result = calc.multiplyList(numbers)

        then:
        result == expected

        and: "Weryfikujemy interakcje"
        // Weryfikujemy, że 'multiply' zostało wywołane tyle razy, ile jest elementów w liście
        // To jest najważniejsza weryfikacja dla Mocka i Spya
        (numbers?.size() ?: 0) * service.multiply(_, _)


        where:
        numbers | expected | type   | override
        [2, 3]  | 6        | 'Mock' | null
       // [4, 5]  | 20       | 'Stub' | null
       // [3, 4]  | 12       | 'Spy'  | null
        // Scenariusz z nadpisaniem dla Spya
      //  [2, 3]  | 999      | 'Spy'  | [a: 2, b: 3, result: 999]
    }
 }