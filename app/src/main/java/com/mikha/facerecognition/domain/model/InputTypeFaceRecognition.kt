package com.mikha.facerecognition.domain.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InputTypeFaceRecognition(
    override val id: Int,
    var isFinished: Boolean = false,
    var photo: Bitmap? = null,
    var rotationDegrees: Float = 0F
): InputType(), Parcelable
