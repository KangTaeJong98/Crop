package com.taetae98.cropimageview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.taetae98.cropimageview.view.CropImageView

class MainActivity : AppCompatActivity() {
    private val cropImageView by lazy { findViewById<CropImageView>(R.id.image_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.crop -> {
                Intent(this, ResultActivity::class.java).apply {
                    putExtra("bitmap", cropImageView.croppedBitmap)
                }.also {
                    startActivity(it)
                }
                true
            }
            else -> {
                false
            }
        }
    }
}