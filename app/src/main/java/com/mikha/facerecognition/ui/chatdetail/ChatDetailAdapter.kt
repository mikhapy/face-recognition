package com.mikha.facerecognition.ui.chatdetail

import android.R.attr
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.mikha.facerecognition.R
import com.mikha.facerecognition.databinding.InputtypeFaceBinding
import com.mikha.facerecognition.databinding.InputtypeTextBinding
import com.mikha.facerecognition.domain.model.InputType
import com.mikha.facerecognition.domain.model.InputTypeFaceRecognition
import com.mikha.facerecognition.domain.model.InputTypeText
import android.graphics.drawable.BitmapDrawable

import android.graphics.drawable.Drawable

import android.R.attr.bitmap

import android.graphics.Bitmap




class ChatDetailAdapter(
    var items: List<InputType>,
    var faceRecognitionCallback: FaceRecognitionClick
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = ChatDetailAdapter::class.java.simpleName

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.i(TAG, "onCreateViewHolder: ")
        return when(viewType){
            0 -> InputTypeFaceViewHolder(InputtypeFaceBinding.inflate(LayoutInflater.from(parent.context)))
            else -> InputTypeTextViewHolder(InputtypeTextBinding.inflate(LayoutInflater.from(parent.context)))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder: ")
        when(holder.itemViewType){
            0 -> {
                val item = items[position] as InputTypeFaceRecognition
                (holder as InputTypeFaceViewHolder).bind(item,faceRecognitionCallback)
            }
            else -> {
                val item = items[position] as InputTypeText
                (holder as InputTypeTextViewHolder).bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return if (item is InputTypeFaceRecognition){
            0
        }else {
            1
        }

    }

    override fun getItemCount(): Int = items.size


    class InputTypeTextViewHolder(val viewBinding: InputtypeTextBinding) : RecyclerView.ViewHolder(viewBinding.root){
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.inputtype_text
        }

        fun bind(inputTypeText: InputTypeText){
            viewBinding.textMessage.text = inputTypeText.text
        }

    }

    class InputTypeFaceViewHolder(val viewBinding: InputtypeFaceBinding) : RecyclerView.ViewHolder(viewBinding.root){
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.inputtype_face
        }

        fun bind(
            inputType: InputTypeFaceRecognition,
            faceRecognitionCallback: FaceRecognitionClick
        ){
            if (inputType.isFinished){
                viewBinding.btnOpenCamera.visibility = View.GONE
                viewBinding.imgResult.setImageBitmap(inputType.photo)
                viewBinding.imgResult.rotation = inputType.rotationDegrees
                viewBinding.imgResult.scaleType = ImageView.ScaleType.CENTER_CROP
                viewBinding.imgResult.adjustViewBounds = true
                viewBinding.imgResultBox.visibility = View.VISIBLE
            }else{
                viewBinding.btnOpenCamera.visibility = View.VISIBLE
                viewBinding.imgResultBox.visibility = View.GONE
            }
            viewBinding.btnOpenCamera.setOnClickListener {
                faceRecognitionCallback.onClick(inputType)
            }
        }
    }

    class FaceRecognitionClick(val click: (InputTypeFaceRecognition) -> Unit){
        fun onClick(item: InputTypeFaceRecognition) = click(item)
    }

}