import spock.lang.Specification
import spock.lang.Shared

class LifecycleAndSharedSpec extends Specification {

    @Shared
    List<Integer> sharedList = []

    def localList

    def setupSpec() {
        println "[setupSpec] Executed once before all tests"
        sharedList << 1
    }

    def cleanupSpec() {
        println "[cleanupSpec] Executed once after all tests"
        sharedList.clear()
    }

    def setup() {
        println "[setup] Executed before each test"
        localList = []
    }

    def cleanup() {
        println "[cleanup] Executed after each test"
        localList.clear()
    }

    def "test shared list is initialized once"() {
        expect:
        sharedList.size() == 1
        sharedList[0] == 1
    }

    def "test local list is empty on each test"() {
        expect:
        localList.isEmpty()
    }

    def "should throw exception and verify message"() {
        when:
        throw new IllegalStateException("Something went wrong")

        then:
        def e = thrown(IllegalStateException)
        e.message == "Something went wrong"
    }

    def "modify shared list"() {
        when:
        sharedList << 42

        then:
        sharedList.contains(42)
    }
}
