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
        fotoapparat = Fotoapparat(
            context = requireContext(),
            view = binding.cameraPreview,                   // view which will draw the camera preview
            scaleType = ScaleType.CenterCrop,    // (optional) we want the preview to fill the view
            lensPosition = front(),              // (optional) define an advanced configuration
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
            photoResult.toBitmap().whenAvailable { bitmapPhoto ->
                bitmapPhoto?.bitmap?.let { it1 ->

                    binding.imgResult.setImageBitmap(it1)
                    binding.imgResult.rotation = (-bitmapPhoto.rotationDegrees).toFloat()
                    binding.cameraPreview.visibility = View.INVISIBLE
                    validateFacesOnImage(it1,(-bitmapPhoto.rotationDegrees).toFloat())
                }
            }
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

    @MainThread
    private fun cameraFailState(){
        Log.i(TAG, "cameraFailState: ")
        binding.cameraPreview.visibility = View.GONE
        binding.btnTakePicture.visibility = View.GONE
        binding.btnTakeAnotherPicture.visibility = View.VISIBLE
        binding.btnSend.visibility = View.GONE
    }

    @MainThread
    private fun cameraSuccessState(){
        Log.i(TAG, "cameraSuccessState: ")
        binding.cameraPreview.visibility = View.GONE
        binding.btnTakePicture.visibility = View.GONE
        binding.btnTakeAnotherPicture.visibility = View.VISIBLE
        binding.btnSend.visibility = View.VISIBLE
    }

    private fun validateFacesOnImage(bitmap: Bitmap, rotationDegrees: Float){

        val rotateBitmap = bitmap.rotateBitmap(rotationDegrees)
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val image = InputImage.fromBitmap(rotateBitmap,0)
        val detector = FaceDetection.getClient(highAccuracyOpts)

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.size > 0) {
                    cameraSuccessState()
                    inputTypeFaceRecognition.isFinished = true
                    inputTypeFaceRecognition.photo = bitmap
                    inputTypeFaceRecognition.rotationDegrees = rotationDegrees
                }else{
                    Toast.makeText(requireContext(), R.string.detect_failed, Toast.LENGTH_LONG).show()
                    cameraFailState()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "validateFacesOnImage: ${e.message}", e)
                cameraFailState()
            }

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