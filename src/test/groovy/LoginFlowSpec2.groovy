import spock.lang.*

@Stepwise
class LoginFlowSpec2 extends Specification {

    static boolean registered = false
    static boolean loggedIn = false
    static boolean resetBetweenSteps = false

    def setup() {
        if (resetBetweenSteps) {
            registered = false
            loggedIn = false
        }
    }

    def "step 1 - user registers successfully"() {
        expect:
        !registered

        when:
        registered = true

        then:
        registered
    }

    def "step 2 - user logs in after registration"() {
        expect:
        registered

        when:
        loggedIn = true

        then:
        loggedIn
    }

    def "step 3 - enable resetBetweenSteps for following test"() {
        when:
        resetBetweenSteps = true

        then:
        true
    }

    def "step 4 - verify state has been reset"() {
        expect:
        !registered
        !loggedIn
    }

    def "step 5 - verify login denied after reset"() {
        expect:
        "Access denied" == (loggedIn ? "Access granted" : "Access denied")

        cleanup:
        resetBetweenSteps = false
    }
}
