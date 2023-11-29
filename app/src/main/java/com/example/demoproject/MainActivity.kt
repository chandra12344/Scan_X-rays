package com.example.demoproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.demoproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initView()
onClick();
        setContentView(binding.root)
    }

    private fun initView() {
        sharedPreferences = getSharedPreferences(Constant.PREFS_FILENAME, Context.MODE_PRIVATE)
    }

    private fun onClick() {
        val savedData = sharedPreferences.getBoolean(Constant.LOG_IN, false)
        binding.newPatient.setOnClickListener {
            val intent = Intent(this, PatientForm::class.java)
             startActivity( intent)
        }
        binding.oldPatient.setOnClickListener {
            val intent = Intent(this, OldPatientActivity::class.java)
            startActivity( intent)
        }
        binding.header.profile.setOnClickListener {
            if (savedData){
                startActivity( Intent(this, ProfileActivity::class.java))
            }else{
                startActivity( Intent(this, OldPatientActivity::class.java))
            }

        }
    }
}