package com.example.stickerviewdemo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.stickerviewdemo.databinding.ActivityMainBinding
import com.example.stickerviewdemo.stickerview.StickerUtils
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    lateinit var binding: ActivityMainBinding

    private val requestImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val uri = it.data?.data as Uri
            val bitmap = StickerUtils.getImage(this, uri)
            binding.stickerView.setBackground(bitmap!!)
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAlbum.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            requestImageLauncher.launch(intent)
        }

        binding.fabAdd.setOnClickListener {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.wind)
            binding.stickerView.addSticker(bitmap)
        }
    }
}