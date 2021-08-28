package com.taetae98.cropimageview.fragment

import androidx.fragment.app.activityViewModels
import com.taetae98.cropimageview.R
import com.taetae98.cropimageview.databinding.BindingFragment
import com.taetae98.cropimageview.databinding.FragmentResultBinding
import com.taetae98.cropimageview.viewmodel.BitmapViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : BindingFragment<FragmentResultBinding>(R.layout.fragment_result) {
    private val viewModel by activityViewModels<BitmapViewModel>()

    override fun onCreateViewDataBinding() {
        super.onCreateViewDataBinding()
        binding.bitmap = viewModel.bitmap.value
    }
}