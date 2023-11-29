package com.example.demoproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.core.os.postDelayed
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.demoproject.databinding.ActivitySplaceBinding

class SplaceActivity : AppCompatActivity() {
    lateinit var binding: ActivitySplaceBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySplaceBinding.inflate(layoutInflater)
        initView()
        setContentView(binding.root)
    }

    private fun initView() {
        if (! Python.isStarted()) {
            Python.start( AndroidPlatform(this));
        }
        sharedPreferences = getSharedPreferences(Constant.PREFS_FILENAME, Context.MODE_PRIVATE)
        val savedData = sharedPreferences.getBoolean(Constant.LOG_IN, false)
        Handler().postDelayed({
            if (savedData){
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        },5000)

    }
}