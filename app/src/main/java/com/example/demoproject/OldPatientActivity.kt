package com.example.demoproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.demoproject.Constant.KEY_DATA
import com.example.demoproject.Constant.LOG_IN
import com.example.demoproject.Constant.PREFS_FILENAME
import com.example.demoproject.databinding.ActivityOldPatientBinding
import com.example.demoproject.model.NewPatient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OldPatientActivity : AppCompatActivity() {
    lateinit var binding: ActivityOldPatientBinding
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Patient Details")
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOldPatientBinding.inflate(layoutInflater)
        initView()
        onClick()
        setContentView(binding.root)
    }

    private fun initView() {
        sharedPreferences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
    }
    private fun onClick() {
        binding.oldPatient.setOnClickListener {
            if (binding.cNOP.text!!.isEmpty()){
                Toast.makeText(this, "Enter Case Number", Toast.LENGTH_SHORT).show()
            }else{
            readUserData(binding.cNOP.text.toString())}
        }
    }

    private fun readUserData(userId: String) {
        val userReference = databaseReference.child(userId)
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val editor = sharedPreferences.edit()
                    editor.putString(KEY_DATA, userId)
                    editor.putBoolean(LOG_IN, true)
                    editor.apply()
                    startActivity(Intent(this@OldPatientActivity,ProfileActivity::class.java))

                } else {
                    Toast.makeText(this@OldPatientActivity, "Case Number Not exist!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error: ${databaseError.message}")
            }
        })
    }
}