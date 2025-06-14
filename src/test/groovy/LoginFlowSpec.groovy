import spock.lang.*

@Stepwise
class LoginFlowSpec extends Specification {

    static boolean registered = false
    static boolean loggedIn = false

    def "user registers successfully"() {
        expect:
        !registered

        when:
        registered = true

        then:
        registered
    }

    def "user logs in only after registration"() {
        expect:
        registered

        when:
        loggedIn = true

        then:
        loggedIn
    }

    def "user can access dashboard only if logged in"() {
        expect:
        loggedIn

        and:
        "Access granted" == (loggedIn ? "Access granted" : "Access denied")
    }

    def "user logs out"() {
        when:
        loggedIn = false

        then:
        !loggedIn
    }

    def "user cannot access dashboard after logout"() {
        expect:
        !loggedIn

        and:
        "Access denied" == (loggedIn ? "Access granted" : "Access denied")
    }
}
