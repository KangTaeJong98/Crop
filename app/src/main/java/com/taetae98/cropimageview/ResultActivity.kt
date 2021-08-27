package com.taetae98.cropimageview

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView

class ResultActivity : AppCompatActivity() {
    private val imageView by lazy { findViewById<ShapeableImageView>(R.id.image_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        intent.getParcelableExtra<Bitmap>("bitmap").also {
            imageView.setImageBitmap(it)
        }
    }
}