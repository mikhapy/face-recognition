package com.mikha.facerecognition.domain.usecase

import com.mikha.facerecognition.domain.model.InputType

interface GetMessages {
    fun getMessages(): List<InputType>
}