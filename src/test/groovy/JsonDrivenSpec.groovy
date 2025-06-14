import spock.lang.*
import groovy.json.JsonSlurper

class JsonDrivenSpec extends Specification {

    @Unroll
    def "should add #a and #b to get #result (JSON)"() {
        expect:
        a + b == result

        where:
        [a, b, result] << readJson("src/test/resources/data.json")
    }

    static List<List> readJson(String path) {
        def file = new File(path)
        new JsonSlurper().parse(file).collect {
            [it.a, it.b, it.result]
        }
    }
}
