package com.mikha.facerecognition.domain.usecase

import com.mikha.facerecognition.domain.model.InputType

interface GetMessages {
    abstract fun getMessages(): List<InputType>
}