import spock.lang.Specification
import spock.util.concurrent.PollingConditions
import spock.lang.Timeout
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean

class UserServiceSpec5 extends Specification {

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
        1 * service.sendConfirmation("first") >> { println "first done" }
        1 * service.sendConfirmation("second") >> { println "second done" }
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

    def "enforce strict call order using then blocks"() {
        given:
        def service = Mock(EmailService)

        when:
        service.sendConfirmation("a")
        service.sendConfirmation("b")

        then:
        1 * service.sendConfirmation("a") >> "sent a"

        then:
        1 * service.sendConfirmation("b") >> "sent b"
    }

    def "should complete within timeout"() {
        given:
        def emailService = Mock(EmailService)
        emailService.sendConfirmation(_) >> {
            Thread.sleep(500) // simulate delay
            return true
        }
        def userService = new UserService(emailService: emailService)

        expect:
        userService.registerUser("timeout@test.com")
    }

    @Timeout(2)
    def "test completes within 2 seconds"() {
        expect:
        Thread.sleep(1000) // should pass
        true
    }

    def "polling async condition"() {
        given:
        def result = new AtomicBoolean(false)

        // async job
        Thread.start {
            Thread.sleep(800)
            result.set(true)
        }

        expect:
        new PollingConditions(timeout: 2).eventually {
            assert result.get()
        }
    }
}
