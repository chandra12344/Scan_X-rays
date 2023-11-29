package com.example.demoproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.demoproject.database.Item
import com.example.demoproject.database.newPatient
import com.example.demoproject.databinding.ActivityPatientFormBinding
import com.example.demoproject.model.NewPatient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class PatientForm : AppCompatActivity() {
    lateinit var binding: ActivityPatientFormBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Patient Details")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientFormBinding.inflate(layoutInflater)
        initView()
        setContentView(binding.root)
    }

    private fun initView() {
        sharedPreferences = getSharedPreferences(Constant.PREFS_FILENAME, Context.MODE_PRIVATE)
        readItems()
        onClick()
    }

    fun readItems() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {

                    Log.d("name44444444444", "${data.value}")

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PatientForm, "something went wrong!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun readUserData(userId: String) {
        val userReference = databaseReference.child(userId)

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // dataSnapshot contains the data at the specified database reference
                if (dataSnapshot.exists()) {
                    Toast.makeText(this@PatientForm, "User already exist!", Toast.LENGTH_SHORT).show()
                } else {
                    val newItemRef = databaseReference.child("${binding.caseNumber.text}")
                    newItemRef.setValue(
                        NewPatient(
                            "${binding.name.text}",
                            "${binding.age.text}",
                            "${binding.caseNumber.text}"
                        )
                    )
                    val editor = sharedPreferences.edit()
                    editor.putString(Constant.KEY_DATA, userId)
                    editor.putBoolean(Constant.LOG_IN, true)
                    editor.apply()
                    startActivity(Intent(this@PatientForm,StartScanActivity::class.java))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors here
                println("Error: ${databaseError.message}")
            }
        })
    }

    private fun onClick() {
        binding.newPatient.setOnClickListener {
            if (binding.name.text!!.isEmpty()) {
                Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show()
            } else if (binding.age.text!!.isEmpty()) {
                Toast.makeText(this, "Enter Age", Toast.LENGTH_SHORT).show()
            } else if (binding.caseNumber.text!!.isEmpty()) {
                Toast.makeText(this, "Enter Case Number", Toast.LENGTH_SHORT).show()
            } else {
                readUserData("${binding.caseNumber.text}")
            }
        }
    }
}