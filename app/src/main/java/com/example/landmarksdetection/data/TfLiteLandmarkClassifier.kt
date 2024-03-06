package com.example.landmarksdetection.data

import android.content.Context
import android.graphics.Bitmap
import android.view.Surface
import android.view.SurfaceControl.TrustedPresentationThresholds
import com.example.landmarksdetection.Domain.Classification
import com.example.landmarksdetection.Domain.LandmarkClassifier
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.gms.vision.classifier.ImageClassifier
import java.lang.IllegalStateException
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions

class TfLiteLandmarkClassifier(
    // TensorFlow LANDMARK Classification related class
    private val context: Context,
    private val thresholds: Float = 0.5f, // Threshold for the 50% probability match
    private val maxResults: Int = 1
) : LandmarkClassifier {

    private var classifier: ImageClassifier? = null
    private fun setupClassifier() {
        val baseOptions = BaseOptions.builder()
            .setNumThreads(2)
            .build()
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(maxResults)
            .setScoreThreshold(thresholds)
            .build()
        try {
            classifier = ImageClassifier.createFromFileAndOptions(
                // create an image classifier from a file which is AI model
                context,
                "Landmarks.tflite",
                options
            )
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun classify(bitmap: Bitmap, rotation: Int): List<Classification> {

        if (classifier == null) {
            setupClassifier()
        }

        val imageProcessor = ImageProcessor.Builder().build()
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()
        val results = classifier?.classify(tensorImage, imageProcessingOptions)

        return results?.flatMap { classifications ->
            classifications.categories.map { category ->
                Classification(
                    name = category.displayName,
                    score = category.score
                )
            }
        }?.distinctBy { it.name } ?: emptyList()
    }
}

private fun getOrientationFromRotation(
    rotation: Int
): ImageProcessingOptions.Orientation {
    return when (rotation) {
        Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.RIGHT_TOP
        Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
        Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
        else -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
    }
}