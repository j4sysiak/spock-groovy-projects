

class MathServiceImpl implements MathService {
    @Override
    int multiply(int a, int b) {
        println "Real multiply: $a * $b"
        return a * b
    }
}
