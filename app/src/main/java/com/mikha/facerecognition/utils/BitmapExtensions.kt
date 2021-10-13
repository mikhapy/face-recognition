package com.mikha.facerecognition.utils

import android.R
import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.rotateBitmap(angle:Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(R.attr.angle.toFloat())
    return Bitmap.createBitmap(this, 0, 0, this.getWidth(), this.getHeight(), matrix, true)
}