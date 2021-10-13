package com.mikha.facerecognition.ui.chatdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mikha.facerecognition.domain.model.InputType
import com.mikha.facerecognition.domain.model.InputTypeFaceRecognition
import com.mikha.facerecognition.domain.usecase.GetMessagesImpl

class ChatDetailViewModel : ViewModel() {

    private var getMessagesImpl = GetMessagesImpl()

    private var _inputTypes = MutableLiveData<List<InputType>>()
    val inputTypes: LiveData<List<InputType>>
        get() = _inputTypes


    init {
        getInputTypes()
    }

    private fun getInputTypes(){
        val messages = getMessagesImpl.getMessages()
        _inputTypes.value = messages
    }

    fun updateInputType(result: InputTypeFaceRecognition?) {
        result?.let {inputType ->
            val current = inputTypes.value?.toMutableList()
            val index = current?.indexOfFirst { it.id == inputType.id }
            index?.let {
                current.set(it,inputType)
                _inputTypes.value = current.toList()
            }
        }

    }
}