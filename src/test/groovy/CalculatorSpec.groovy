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

    def "demonstrate shared object and lifecycle hooks"() {
        expect:
        sharedCalculator.multiply(2, 5) == 10
    }
}