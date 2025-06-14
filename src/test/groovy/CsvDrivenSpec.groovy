import spock.lang.*
import java.nio.file.*

class CsvDrivenSpec extends Specification {

    @Unroll
    def "should add #a and #b to get #result (CSV)"() {
        expect:
        a + b == result

        where:
        [a, b, result] << readCsv("src/test/resources/data.csv")
    }

    static List<List> readCsv(String path) {
        return Files.readAllLines(Paths.get(path))
                .drop(1) // skip header
                .collect { line ->
                    def parts = line.split(',')
                    [parts[0].toInteger(), parts[1].toInteger(), parts[2].toInteger()]
                }
    }
}
