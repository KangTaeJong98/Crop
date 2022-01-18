package com.taetae98.crop.fragment

import androidx.fragment.app.activityViewModels
import com.taetae98.crop.R
import com.taetae98.crop.databinding.FragmentResultBinding
import com.taetae98.crop.viewmodel.CropImageViewModel
import com.taetae98.modules.library.binding.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : BindingFragment<FragmentResultBinding>(R.layout.fragment_result) {
    private val viewModel by activityViewModels<CropImageViewModel>()

    override fun onCreateViewDataBinding() {
        super.onCreateViewDataBinding()
        binding.bitmap = viewModel.croppedImage.value
    }
}