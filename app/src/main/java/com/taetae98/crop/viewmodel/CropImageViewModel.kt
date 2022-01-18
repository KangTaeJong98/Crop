package com.taetae98.crop.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CropImageViewModel @Inject constructor(
    stateHandle: SavedStateHandle
) : ViewModel() {
    val imageURI by lazy { MutableLiveData<Uri>() }
    val croppedImage by lazy { MutableLiveData<Bitmap>() }
}