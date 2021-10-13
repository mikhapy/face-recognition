package com.mikha.facerecognition.ui.chatdetail.inputtypes.selfie

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.mikha.facerecognition.R
import com.mikha.facerecognition.ui.chatdetail.inputtypes.selfie.utils.FaceRecognitionState
import com.mikha.facerecognition.utils.rotateBitmap
import io.fotoapparat.result.PhotoResult
import kotlinx.coroutines.launch

class SelfieFaceRecognitionModalFragmentViewModel: ViewModel() {

    private val TAG = "SelfieFaceVM"

    private var _faceRecognitionState = MutableLiveData<FaceRecognitionState<Bitmap>>()
    val faceRecognitionState: LiveData<FaceRecognitionState<Bitmap>>
        get() = _faceRecognitionState



    fun validateImage(photoResult: PhotoResult){
        viewModelScope.launch {
            photoResult.toBitmap().whenAvailable { bitmapPhoto ->
                bitmapPhoto?.bitmap?.let { it1 ->
                    _faceRecognitionState.postValue(FaceRecognitionState.analyzing(it1,(-bitmapPhoto.rotationDegrees).toFloat()))
                    validateFacesOnImage(it1,(-bitmapPhoto.rotationDegrees).toFloat())
                }
            }
        }
    }

    private fun validateFacesOnImage(bitmap: Bitmap, rotationDegrees: Float){

        val rotateBitmap = bitmap.rotateBitmap(rotationDegrees)
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val image = InputImage.fromBitmap(rotateBitmap,0)
        val detector = FaceDetection.getClient(highAccuracyOpts)

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.size > 0) {
                    Log.e(TAG, "validateFacesOnImage Success")
                    _faceRecognitionState.postValue(FaceRecognitionState.success(bitmap,rotationDegrees))
                }else{
                    Log.e(TAG, "validateFacesOnImage Error")
                    _faceRecognitionState.postValue(FaceRecognitionState.error())
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "validateFacesOnImage: ${e.message}", e)
                _faceRecognitionState.postValue(FaceRecognitionState.error())
            }

    }

}