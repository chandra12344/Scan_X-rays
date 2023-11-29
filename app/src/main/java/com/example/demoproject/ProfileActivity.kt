package com.example.demoproject

import ItemAdapter
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.demoproject.Constant.KEY_DATA
import com.example.demoproject.Constant.PREFS_FILENAME
import com.example.demoproject.database.Item
import com.example.demoproject.databinding.ActivityProfileBinding
import com.example.demoproject.model.PatientData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var binding: ActivityProfileBinding
    private val pic_id = 123
    lateinit var adapter :CustomAdapter
    var patientData = ArrayList<PatientData>()
    private val CAMERA_PERMISSION_CODE = 100
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Patient Details")
    private val databaseReference2: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Patient Details Data")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityProfileBinding.inflate(layoutInflater)
        initView()
        onClick()
        setContentView(binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun onClick() {
        binding.newScan.setOnClickListener {
            showCustomDialog()
//            checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)
//            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//            changeImage.launch(pickImg)
        }
        binding.logout.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putBoolean(Constant.LOG_IN, false)
            editor.apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity( intent)
            finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pic_id) {
            // BitMap is data structure of image file which store the image in memory
            val photo = data?.extras!!["data"] as Bitmap?
            Constant.bitmap=photo
            Constant.value+=1;
            val intent = Intent(this, ResultActivity::class.java)
            startActivity( intent)

        }
    }
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@ProfileActivity, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@ProfileActivity, arrayOf(permission), requestCode)
        } else {
            val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(camera_intent, pic_id)
//            Toast.makeText(this@StartScanActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this@StartScanActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@ProfileActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.P)
    private val changeImage =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                val source = imgUri?.let { it1 -> ImageDecoder.createSource(this.contentResolver, it1) }
                val bitmap = source?.let { it1 -> ImageDecoder.decodeBitmap(it1) }
                Constant.bitmap=bitmap
                Constant.value+=1;
                val intent = Intent(this, ResultActivity::class.java)
                startActivity( intent)
            }
        }
    private fun initView() {
        sharedPreferences = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        val savedData = sharedPreferences.getString(KEY_DATA, "")
        readUserData("$savedData")
        readData("$savedData")
//        val itemList = listOf(Item("01/02/2023","35 Degrees"), Item("01/04/2023","40 Degrees"), Item("01/06/2023","52 Degrees"))
        adapter = CustomAdapter(patientData)
        binding.listView.layoutManager = LinearLayoutManager(this)
        binding.listView.adapter = adapter
    }
    private fun readUserData(userId: String) {
        val userReference = databaseReference.child(userId)
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val name = dataSnapshot.child("name").getValue(String::class.java)
                    val age = dataSnapshot.child("age").getValue(String::class.java)
                    val caseNumber = dataSnapshot.child("caseNumber").getValue(String::class.java)
                    binding.name.text=name;
                    binding.age.text="${age} years || C.N. > ${caseNumber}";

                }else{
                    Toast.makeText(this@ProfileActivity, "No data found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun readData(userId: String) {
        val userReference = databaseReference2.child(userId)
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (data in dataSnapshot.children) {
                        val item = data.getValue(PatientData::class.java)
                        if (item != null) {
                            patientData.add(PatientData("${item.date}","${item.angle}"))
                        }
                        // Handle retrieved item
                    }
                    adapter.notifyDataSetChanged()

                }else{
                    Toast.makeText(this@ProfileActivity, "No data found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun showCustomDialog() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_layout)
        val width = resources.displayMetrics.widthPixels * 0.8
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        val layoutParams = LinearLayout.LayoutParams(width.toInt(), height.toInt())
        dialog.window?.setLayout(layoutParams.width, layoutParams.height)

        val camera = dialog.findViewById<AppCompatButton>(R.id.Pcamera)
        val gallery = dialog.findViewById<AppCompatButton>(R.id.Pgallery)
         camera.setOnClickListener {
             checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)
             dialog.dismiss()
         }
        gallery.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
            dialog.dismiss()
        }

        dialog.show()
    }
    override fun onBackPressed() {
        finishAffinity()
    }

    private fun showConfirmationDialog() {
        finish()
    }
}