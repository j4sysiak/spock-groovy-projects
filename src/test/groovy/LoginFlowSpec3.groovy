import spock.lang.*

@Stepwise
class LoginFlowSpec3 extends Specification {

    @Shared
    UserSession session = new UserSession()

    static boolean resetBetweenSteps = false

    def setup() {
        if (resetBetweenSteps) {
            session.reset()
        }
    }

    def "step 1 - user registers successfully"() {
        expect:
        !session.registered

        when:
        session.registered = true

        then:
        session.registered
    }

    def "step 2 - user logs in after registration"() {
        expect:
        session.registered

        when:
        session.loggedIn = true

        then:
        session.loggedIn
    }

    def "step 3 - enable resetBetweenSteps for following test"() {
        when:
        resetBetweenSteps = true

        then:
        true
    }

    def "step 4 - verify session state has been reset"() {
        expect:
        !session.registered
        !session.loggedIn
    }

    def "step 5 - verify access denied after reset"() {
        expect:
        "Access denied" == (session.loggedIn ? "Access granted" : "Access denied")

        cleanup:
        resetBetweenSteps = false
    }
}
