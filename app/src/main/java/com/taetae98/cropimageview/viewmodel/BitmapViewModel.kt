package com.taetae98.cropimageview.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BitmapViewModel @Inject constructor(
    stateHandle: SavedStateHandle
) : ViewModel() {
    val bitmap by lazy { MutableLiveData<Bitmap>() }
}