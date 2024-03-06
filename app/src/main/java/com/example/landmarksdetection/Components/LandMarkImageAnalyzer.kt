package com.example.landmarksdetection.Components

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.landmarksdetection.Domain.Classification
import com.example.landmarksdetection.Domain.LandmarkClassifier

// This is our camera X related class
// This class will be called for every single frame, and we will convert it into
// out bitmap and feed it into our tensorflow classifier
class LandMarkImageAnalyzer(
    private val classifier: LandmarkClassifier,
    private val onResults: (List<Classification>) -> Unit
) : ImageAnalysis.Analyzer {

    // will tell us how many frames have passed
    private var frameSkipCounter = 0

    override fun analyze(image: ImageProxy) {
        if(frameSkipCounter %60 == 0){
            val rotationDegrees = image.imageInfo.rotationDegrees
            val bitmap = image
                .toBitmap()
                .centerCrop(321,321)

            val results = classifier.classify(bitmap,rotationDegrees)
            onResults(results)

        }
        frameSkipCounter += 1
        image.close()
    }


}