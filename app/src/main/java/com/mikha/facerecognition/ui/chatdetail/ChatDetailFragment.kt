package com.mikha.facerecognition.ui.chatdetail

import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikha.facerecognition.databinding.ChatDetailFragmentBinding
import com.mikha.facerecognition.domain.model.InputType
import com.mikha.facerecognition.domain.model.InputTypeFaceRecognition
import com.mikha.facerecognition.domain.model.InputTypeText

class ChatDetailFragment : Fragment() {


    private lateinit var viewModel: ChatDetailViewModel
    private lateinit var binding: ChatDetailFragmentBinding
    private var detailAdapter: ChatDetailAdapter? = null
    private val items: ArrayList<InputType> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatDetailFragmentBinding.inflate(layoutInflater)

        detailAdapter = ChatDetailAdapter(
            items,
            faceRecognitionCallback = ChatDetailAdapter.FaceRecognitionClick {
                findNavController().navigate(
                    ChatDetailFragmentDirections.actionChatDetailFragmentToSelfieFaceRecognitionModalFragment(it)
                )
            }
        )
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = detailAdapter
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<InputTypeFaceRecognition>("key")?.observe(viewLifecycleOwner) {result ->
            viewModel.updateInputType(result)
        }


    }





    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChatDetailViewModel::class.java)
        // TODO: Use the ViewModel

        setupObservers()
    }

    private fun setupObservers(){
        viewModel.inputTypes.observe(viewLifecycleOwner, Observer {
            items.clear()
            items.addAll(it)
            detailAdapter?.notifyDataSetChanged()
        })
    }

}