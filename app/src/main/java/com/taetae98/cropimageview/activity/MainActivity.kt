package com.taetae98.cropimageview.activity

import androidx.navigation.ui.AppBarConfiguration
import com.taetae98.cropimageview.R
import com.taetae98.cropimageview.databinding.ActivityMainBinding
import com.taetae98.cropimageview.databinding.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main) {
    override val homeIds by lazy { setOf(R.id.mainFragment) }
    override val appBarConfiguration by lazy { AppBarConfiguration(homeIds) }
}