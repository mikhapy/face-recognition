package com.mikha.facerecognition.domain.model

data class InputTypeText(
    override val id: Int,
    val text: String
): InputType()