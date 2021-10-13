package com.mikha.facerecognition.ui.chatdetail.inputtypes.selfie.utils

import android.graphics.Bitmap

data class FaceRecognitionState<out T>(val status: Status, val data: Bitmap?, val rotationDegrees: Float = 0f) {

    enum class Status{
        ANALYZING,
        ERROR,
        SUCCESS
    }

    companion object {
        fun <T> analyzing(data: Bitmap, rotationDegrees: Float): FaceRecognitionState<T>{
            return FaceRecognitionState(Status.ANALYZING, data, rotationDegrees)
        }

        fun <T> error(): FaceRecognitionState<T>{
            return FaceRecognitionState(Status.ERROR, null)
        }

        fun <T> success(data: Bitmap, rotationDegrees: Float): FaceRecognitionState<T>{
            return FaceRecognitionState(Status.SUCCESS, data, rotationDegrees)
        }
    }
}