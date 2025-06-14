class UserService {
    EmailService emailService

    boolean registerUser(String email) {
        if (email) {
            emailService.sendConfirmation(email)
            return true
        }
        return false
    }
}
