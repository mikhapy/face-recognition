package com.mikha.facerecognition.ui.chatdetail.inputtypes.selfie

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.mikha.facerecognition.R
import com.mikha.facerecognition.databinding.FragmentSelfieFaceRecognitionModalBinding
import com.mikha.facerecognition.domain.model.InputTypeFaceRecognition
import com.mikha.facerecognition.ui.chatdetail.inputtypes.selfie.utils.FaceRecognitionState
import com.mikha.facerecognition.utils.rotateBitmap
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.front


/**
 * A simple [Fragment] subclass.
 * Use the [SelfieFaceRecognitionModalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelfieFaceRecognitionModalFragment : BottomSheetDialogFragment() {
    private val TAG = "SelfieFace"
    private lateinit var binding: FragmentSelfieFaceRecognitionModalBinding
    private lateinit var fotoapparat: Fotoapparat
    private lateinit var inputTypeFaceRecognition: InputTypeFaceRecognition
    private lateinit var viewModel: SelfieFaceRecognitionModalFragmentViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inputTypeFaceRecognition = (arguments?.get("inputType") as? InputTypeFaceRecognition) ?: throw Exception(getString(R.string.wrong_argument_received))
        isCancelable = false

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSelfieFaceRecognitionModalBinding.inflate(inflater)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startCamera()
        initButtons()
    }

    private fun startCamera(){
        Toast.makeText(context, R.string.center_your_face, Toast.LENGTH_SHORT).show()
        fotoapparat = Fotoapparat(
            context = requireContext(),
            view = binding.cameraPreview,
            scaleType = ScaleType.CenterCrop,
            lensPosition = front(),
            cameraErrorCallback = { error ->
                Log.e(TAG, "onCreate: ${error.message}")
            }
        )
    }


    private fun initButtons(){
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnTakePicture.setOnClickListener {
            val photoResult = fotoapparat.takePicture()
            viewModel.validateImage(photoResult)
        }

        binding.btnTakeAnotherPicture.setOnClickListener {
            cameraInitialState()
        }

        binding.btnSend.setOnClickListener {
             findNavController().previousBackStackEntry?.savedStateHandle?.set("key", inputTypeFaceRecognition)
            dismiss()
        }
    }

    @MainThread
    private fun cameraInitialState(){
        Log.i(TAG, "cameraInitialState: ")
        binding.cameraPreview.visibility = View.VISIBLE
        binding.btnTakePicture.visibility = View.VISIBLE
        binding.btnTakeAnotherPicture.visibility = View.GONE
        binding.btnSend.visibility = View.GONE
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SelfieFaceRecognitionModalFragmentViewModel::class.java)
        setupObservers()
    }

    private fun setupObservers(){
        viewModel.faceRecognitionState.observe(viewLifecycleOwner, Observer {
            when (it.status){
                FaceRecognitionState.Status.SUCCESS -> {
                    binding.cameraPreview.visibility = View.GONE
                    binding.btnTakePicture.visibility = View.GONE
                    binding.btnTakeAnotherPicture.visibility = View.VISIBLE
                    binding.btnSend.visibility = View.VISIBLE
                    inputTypeFaceRecognition.isFinished = true
                    inputTypeFaceRecognition.photo = it.data
                    inputTypeFaceRecognition.rotationDegrees = it.rotationDegrees
                }
                FaceRecognitionState.Status.ERROR -> {
                    Toast.makeText(requireContext(), R.string.detect_failed, Toast.LENGTH_LONG).show()
                    binding.cameraPreview.visibility = View.GONE
                    binding.btnTakePicture.visibility = View.GONE
                    binding.btnTakeAnotherPicture.visibility = View.VISIBLE
                    binding.btnSend.visibility = View.GONE
                }
                FaceRecognitionState.Status.ANALYZING -> {
                    binding.imgResult.setImageBitmap(it.data)
                    binding.imgResult.rotation = (it.rotationDegrees)
                    binding.cameraPreview.visibility = View.INVISIBLE
                }
            }
        })
    }


    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        Handler().postDelayed({
            fotoapparat.start()
        }, 300)

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
        fotoapparat.stop()
    }


}