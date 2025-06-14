import spock.lang.*

@Stepwise
class LoginFlowSpec4 extends Specification {

    @Shared
    File sessionFile = File.createTempFile("session", ".txt")

    static boolean resetBetweenSteps = false

    def setup() {
        if (resetBetweenSteps && sessionFile.exists()) {
            sessionFile.text = ""
        }
    }

    def cleanupSpec() {
        if (sessionFile.exists()) {
            sessionFile.delete()
        }
    }

    def "step 1 - write registration to session file"() {
        expect:
        sessionFile.text == ""

        when:
        sessionFile.text = "registered=true"

        then:
        sessionFile.text.contains("registered=true")
    }

    def "step 2 - append login state"() {
        expect:
        sessionFile.text.contains("registered=true")

        when:
        sessionFile << "\nloggedIn=true"

        then:
        sessionFile.text.contains("loggedIn=true")
    }

    def "step 3 - enable reset for next step"() {
        when:
        resetBetweenSteps = true

        then:
        true
    }

    def "step 4 - verify session file reset"() {
        expect:
        sessionFile.text == ""
    }

    def "step 5 - write denied access to session file"() {
        when:
        sessionFile.text = "access=denied"

        then:
        sessionFile.text == "access=denied"

        cleanup:
        resetBetweenSteps = false
    }
}
