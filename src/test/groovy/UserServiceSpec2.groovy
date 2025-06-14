import spock.lang.Specification

class UserServiceSpec2 extends Specification {

    def "should send confirmation email when registering"() {
        given:
        def emailService = Mock(EmailService)
        def userService = new UserService(emailService: emailService)

        when:
        def result = userService.registerUser("test@example.com")

        then:
        result
        1 * emailService.sendConfirmation("test@example.com")
    }

    def "should not call email service when email is null"() {
        given:
        def emailService = Mock(EmailService)
        def userService = new UserService(emailService: emailService)

        when:
        def result = userService.registerUser(null)

        then:
        !result
        0 * emailService._
    }

    def "should stub email confirmation response"() {
        given:
        def emailService = Stub(EmailService)
        emailService.sendConfirmation(_) >> true
        def userService = new UserService(emailService: emailService)

        expect:
        userService.registerUser("john@example.com")
    }
}
