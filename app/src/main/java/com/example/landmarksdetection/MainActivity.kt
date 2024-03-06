package com.example.landmarksdetection

import android.content.pm.PackageManager
import androidx.compose.ui.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.landmarksdetection.Components.CameraPreview
import com.example.landmarksdetection.Components.LandMarkImageAnalyzer
import com.example.landmarksdetection.Domain.Classification
import com.example.landmarksdetection.data.TfLiteLandmarkClassifier
import com.example.landmarksdetection.ui.theme.LandmarksDetectionTheme
import java.util.jar.Manifest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!hasCameraPermission()){
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.CAMERA),0
            )
        }

        setContent {
            LandmarksDetectionTheme {

                var classifications by remember {
                    mutableStateOf(emptyList<Classification>())
                }
                var analyzer = remember {
                    LandMarkImageAnalyzer(
                        classifier = TfLiteLandmarkClassifier(
                            context = applicationContext
                        ),
                        onResults = {
                            classifications = it
                        }
                    )
                }
                val controller = remember {
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
                        setImageAnalysisAnalyzer(
                            ContextCompat.getMainExecutor(applicationContext),
                            analyzer
                        )
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Red)
                ) {

                    CameraPreview(
                        controller = controller,
                        modifier = Modifier.fillMaxSize()
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center)
                    ){
                        classifications.forEach {
                              Text(text = "Hello".toString())
                            Text(
                                text = it.name,
                                modifier = Modifier.fillMaxWidth().background(color = Color.Green).padding(8.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                            Text(text = it.score.toString())
                        }
                    }

                }

            }
        }
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this, android.Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED


}
