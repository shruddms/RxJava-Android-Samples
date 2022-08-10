package com.kyungeun.rxjava_android_samples.ui.webview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kyungeun.rxjava_android_samples.databinding.ActivityWebviewBinding

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewBinding

    private var link = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = this.intent
        link = intent.extras?.get("link") as String

        if (link.isNotEmpty()) {
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.loadUrl(link)
        } else {
            Toast.makeText(this@WebViewActivity, "Url is empty", Toast.LENGTH_SHORT).show()
        }
    }
}