import spock.lang.Specification

class UserServiceSpec3 extends Specification {

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

    def "verifyAll example with interaction"() {
        given:
        def emailService = Mock(EmailService)
        def userService = new UserService(emailService: emailService)

        when:
        userService.registerUser("verify@example.com")

        then:
        verifyAll {
            1 * emailService.sendConfirmation("verify@example.com")
        }
    }

    def "with block to verify mock internals"() {
        given:
        def emailService = Mock(EmailService)

        when:
        emailService.sendConfirmation("inside@with.com")

        then:
        with(emailService) {
            1 * sendConfirmation("inside@with.com")
        }
    }

    def "interaction ordering example"() {
        given:
        def service = Mock(EmailService)

        when:
        service.sendConfirmation("first")
        service.sendConfirmation("second")

        then:
        1 * service.sendConfirmation("first")
        then:
        1 * service.sendConfirmation("second")
    }

    def "argument capturing example"() {
        given:
        def emailService = Mock(EmailService)
        def userService = new UserService(emailService: emailService)
        String captured = null

        emailService.sendConfirmation(_) >> { args ->
            captured = args[0]
            return true
        }

        when:
        userService.registerUser("captured@example.com")

        then:
        captured == "captured@example.com"
    }
}
