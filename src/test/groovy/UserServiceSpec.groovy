import spock.lang.Specification

class UserServiceSpec extends Specification {

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
}
