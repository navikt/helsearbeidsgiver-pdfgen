import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile
import java.io.File
import kotlin.test.assertEquals

object SharedTestContainer {
    private val projectDir: File = File(".").absoluteFile

    val container: GenericContainer<*> =
        GenericContainer(
            ImageFromDockerfile("helsearbeidsgiver-pdfgen-test", false)
                .withFileFromPath("Dockerfile", projectDir.resolve("Dockerfile").toPath())
                .withFileFromPath("templates", projectDir.resolve("templates").toPath())
                .withFileFromPath("fonts", projectDir.resolve("fonts").toPath())
                .withFileFromPath("resources", projectDir.resolve("resources").toPath()),
        ).apply {
            withExposedPorts(8080)
            waitingFor(Wait.forHttp("/internal/is_ready").forPort(8080))
            // Start the container manually
            start()
            println("Container har blitt startet opp!")
        }

    val endepunkt: String
        get() = "http://${container.host}:${container.getMappedPort(8080)}"
}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Order(1)
class TestContainer {
    @Test
    fun `start TestContainer pdfgen med templates`() {
        SharedTestContainer.container
        assertEquals(1, 1)
    }
}
