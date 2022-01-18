package com.taetae98.crop.fragment

import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.taetae98.crop.R
import com.taetae98.crop.databinding.FragmentMainBinding
import com.taetae98.crop.viewmodel.CropImageViewModel
import com.taetae98.modules.library.binding.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BindingFragment<FragmentMainBinding>(R.layout.fragment_main) {
    private val viewModel by activityViewModels<CropImageViewModel>()

    private val chooseImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        viewModel.imageURI.postValue(it)
    }


    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        if (viewModel.imageURI.value == null) {
            chooseImage.launch("image/*")
        }

        binding.imageView.tag = "Main"
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.crop -> {
                viewModel.croppedImage.value = binding.imageView.croppedBitmap
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToResultFragment())
                true
            }
            else -> {
                false
            }
        }
    }
}