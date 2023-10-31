package com.ben.bencustomerserverapp

import android.app.AppComponentFactory
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.ben.bencustomerserver.model.Constants
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
            with(intent){
                putExtra(Constants.KEY_USER_ID,"symbol-8374782"  )
                putExtra(Constants.KEY_USER_NAME,"symbol2023" )
                putExtra(Constants.KEY_USER_AVATAR,"https://symbol-file.oss-cn-beijing.aliyuncs.com/b1aa0c85f414485bc77a122592eea150.jpg"  )
                putExtra(Constants.KEY_SELLER_CODE,"5c6cbcb7d55ca")
            }
            startActivity(intent)
        }


    }


}