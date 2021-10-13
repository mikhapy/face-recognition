package com.mikha.facerecognition.domain.usecase

import android.util.Log
import com.mikha.facerecognition.domain.model.InputType
import com.mikha.facerecognition.domain.model.InputTypeFaceRecognition
import com.mikha.facerecognition.domain.model.InputTypeText

class GetMessagesImpl: GetMessages {

    override fun getMessages(): List<InputType> {
        val inputTypeText1 = InputTypeText(1,"Lorem Ipsum")
        val inputTypeText2 = InputTypeText(2,"Lorem Ipsum 2")
        val inputTypeText3 = InputTypeText(3,"Lorem Ipsum 3")
        val inputTypeFace = InputTypeFaceRecognition(4)
        return listOf(inputTypeText1,inputTypeText2,inputTypeText3,inputTypeFace)
    }
}