package com.example.visionsignapp.ui.screen.VideoCall

/*import android.content.Context
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker.HandLandmarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private lateinit var handLandmarker: HandLandmarker

suspend fun initializeHandLandmarker(context: Context) {
    withContext(Dispatchers.IO) {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("hand_landmarker.task")
            .build()

        val options = HandLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setNumHands(2)
            .build()

        handLandmarker = HandLandmarker.createFromOptions(context, options)
    }
}*/

// Process the MPImage and detect hand landmarks
/*fun detectHandLandmarks(image: MPImage): HandLandmarker.HandLandmarkerResult {
    return handLandmarker.detect(image)
}*/