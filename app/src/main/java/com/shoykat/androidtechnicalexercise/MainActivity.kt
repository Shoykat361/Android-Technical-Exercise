package com.shoykat.androidtechnicalexercise

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class MainActivity : AppCompatActivity() {
    private lateinit var fetchButton: Button
    private lateinit var imageView: ImageView
    private lateinit var errorTextView: TextView

    private var lastImageURL: String? = null
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fetchButton = findViewById(R.id.button)
        imageView = findViewById(R.id.imageView)
        errorTextView=findViewById(R.id.errorView)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        fetchButton.setOnClickListener {
            fetchRandomImage()
        }
        val lastImageUrl = sharedPreferences.getString("lastImageURL", null)
        if (lastImageUrl != null) {
            loadImageFromURL(lastImageUrl)
        }
        lastImageURL = getLastCachedImageURL()
        if (lastImageURL != null) {
            loadImageFromURL(lastImageURL!!)
        } else {
            fetchRandomImage()
        }

    }
    private fun fetchRandomImage() {
        val randomImageUrl = "https://picsum.photos/200/300?${System.currentTimeMillis()}"
        lastImageURL = randomImageUrl
        if (isNetworkAvailable()) {
            errorTextView.visibility = View.GONE

            val randomImageUrl = "https://picsum.photos/200/300?${System.currentTimeMillis()}"
            lastImageURL = randomImageUrl

            loadImageFromURL(randomImageUrl)
            cacheLastImageURL(randomImageUrl)
        } else {
            errorTextView.visibility = View.VISIBLE
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun loadImageFromURL(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(200, 300)
            .into(imageView)
    }

    override fun onDestroy() {
        super.onDestroy()
        lastImageURL?.let { cacheLastImageURL(it) }
    }

    private fun cacheLastImageURL(imageUrl: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("lastImageURL", imageUrl)
        editor.apply()
    }

    private fun getLastCachedImageURL(): String? {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("lastImageURL", null)
    }
}
