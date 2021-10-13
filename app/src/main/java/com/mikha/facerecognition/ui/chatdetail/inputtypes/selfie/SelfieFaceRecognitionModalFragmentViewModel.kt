package com.mikha.facerecognition.ui.chatdetail.inputtypes.selfie

import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikha.facerecognition.ui.chatdetail.inputtypes.selfie.utils.FaceRecognitionState
import io.fotoapparat.result.PhotoResult
import kotlinx.coroutines.launch

class SelfieFaceRecognitionModalFragmentViewModel: ViewModel() {

    private var _faceRecognitionState = MutableLiveData<FaceRecognitionState<Bitmap>>()
    val faceRecognitionState: LiveData<FaceRecognitionState<Bitmap>>
        get() = _faceRecognitionState



    fun validateImage(photoResult: PhotoResult){
        viewModelScope.launch {
            photoResult.toBitmap().whenAvailable { bitmapPhoto ->
                bitmapPhoto?.bitmap?.let { it1 ->
                    _faceRecognitionState.postValue(FaceRecognitionState.analyzing(it1,(-bitmapPhoto.rotationDegrees).toFloat()))

//                    validateFacesOnImage(it1,(-bitmapPhoto.rotationDegrees).toFloat())
                }
            }
        }
    }

}