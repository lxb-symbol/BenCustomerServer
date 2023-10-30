package com.ben.bencustomerserverapp

import android.app.AppComponentFactory
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.ben.bencustomerserver.ui.ChatActivity
import com.ben.bencustomerserver.utils.ContextUtils
import com.ben.bencustomerserverapp.databinding.ActivityTestBinding
import kotlinx.coroutines.flow.combine

class TestActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextUtils.setContextInstance(this)
        binding = ActivityTestBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.tvGo.setOnClickListener {
            val intent = Intent(this@TestActivity, ChatActivity::class.java)
            startActivity(intent)
        }


    }


}