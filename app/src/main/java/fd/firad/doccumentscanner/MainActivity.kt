package fd.firad.doccumentscanner

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import fd.firad.doccumentscanner.ui.theme.DoccumentScannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoccumentScannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {

                    val context = LocalContext.current
                    val scannerMode by remember { mutableIntStateOf(GmsDocumentScannerOptions.SCANNER_MODE_FULL) }
                    val scannerLauncher =
                        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                            if (result.resultCode == RESULT_OK) {
                                val gmsResult =
                                    GmsDocumentScanningResult.fromActivityResultIntent(result.data) // get the result
                                gmsResult?.pages?.let { pages ->
                                    pages.forEach { page ->
                                        val imageUri = page.imageUri
                                    }
                                }
                                gmsResult?.pdf?.let { pdf ->
                                    val pdfUri = pdf.uri
                                }
                            }
                        }

                    CenteredButton(onclick = {
                        val scannerOptions =
                            GmsDocumentScannerOptions.Builder().setGalleryImportAllowed(true)
                                .setPageLimit(1).setResultFormats(
                                    GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                                    GmsDocumentScannerOptions.RESULT_FORMAT_PDF
                                ).setScannerMode(scannerMode).build()
                        val scanner = GmsDocumentScanning.getClient(scannerOptions)
                        scanner.getStartScanIntent(context as Activity)
                            .addOnSuccessListener { intentSender ->
                                scannerLauncher.launch(
                                    IntentSenderRequest.Builder(intentSender).build()
                                )
                            }.addOnFailureListener {

                            }
                    }, text = "Scan Now")
                }
            }
        }
    }
}

@Composable
fun CenteredButton(onclick: () -> Unit, text: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { onclick.invoke() }) {
            Text(text)
        }
    }
}
