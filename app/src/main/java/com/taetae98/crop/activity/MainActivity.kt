package com.taetae98.crop.activity

import com.taetae98.crop.R
import com.taetae98.crop.databinding.ActivityMainBinding
import com.taetae98.modules.library.navigation.NavigationActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : NavigationActivity<ActivityMainBinding>(R.layout.activity_main, R.id.fragment)