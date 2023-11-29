package com.example.demoproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.demoproject.databinding.ActivityResultBinding
import com.example.demoproject.model.NewPatient
import com.example.demoproject.model.PatientData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.random.Random

class ResultActivity : AppCompatActivity() {
    lateinit var binding: ActivityResultBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Patient Details Data")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityResultBinding.inflate(layoutInflater)
        initView()
        setContentView(binding.root)
    }

    private fun initView() {
        sharedPreferences = getSharedPreferences(Constant.PREFS_FILENAME, Context.MODE_PRIVATE)
        val savedData = sharedPreferences.getString(Constant.KEY_DATA, "")
        databaseReference=databaseReference.child("$savedData")
        val py= Python.getInstance();
        val module=py.getModule("script")
//        val number = module["number"]?.toInt()
        val calculate_cobb_angle = module["calculate_cobb_angle"]
        Log.d("ImagePath", saveBitmapToFile(this,Constant.bitmap) ?: "Path not found")
        val angle=calculate_cobb_angle?.call( saveBitmapToFile(this,Constant.bitmap))
        if (angle != null) {
            binding.angle.text="${angle.toDouble().toInt().absoluteValue} Degrees"
        }
        if (angle != null) {
            if (angle.toDouble().toInt().absoluteValue<21){
                binding.severe.text="Mild Scoliosis"
            }else if (angle.toDouble().toInt().absoluteValue<41){
                binding.severe.text="Moderate Scoliosis"
            }else{
                binding.severe.text="Severe Scoliosis"
            }
        }
        binding.save.setOnClickListener {
            val sdf = SimpleDateFormat("dd-M-yyyy")
            val currentDate = sdf.format(Date())
            val newItemRef = databaseReference.push()
            newItemRef.setValue(
                PatientData("$currentDate","${binding.angle.text}")
            )
           startActivity(Intent(this@ResultActivity,ProfileActivity::class.java))

        }
        binding.img.setImageBitmap(Constant.bitmap)
    }
    private fun saveBitmapToFile(context: Context, bitmap: Bitmap): String? {
        // Create a file to save the bitmap
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        try {
            val file = File(storageDir, fileName)
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream)
            fileOutputStream.close()
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}