import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import java.time.Duration

fun ByteArray.toText(): String {
    val pdfDocument = Loader.loadPDF(this)
    val textStripper = PDFTextStripper()
    val extractedText = textStripper.getText(pdfDocument)
    pdfDocument.close()
    return extractedText
}

fun makePdfRequest(
    route: String,
    jsonBody: String,
): ByteArray {
    val client =
        OkHttpClient
            .Builder()
            .connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(30))
            .writeTimeout(Duration.ofSeconds(10))
            .build()

    // start test container om den ikke allerede er startet
    SharedTestContainer.container
    val url = "${SharedTestContainer.endepunkt}$route"

    val request =
        Request
            .Builder()
            .url(url)
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

    client.newCall(request).execute().use { response ->
        if (response.code != 200) {
            throw RuntimeException("Expected HTTP 200 OK but got ${response.code}")
        }

        return response.body?.bytes()
            ?: throw RuntimeException("Response body was empty")
    }
}
