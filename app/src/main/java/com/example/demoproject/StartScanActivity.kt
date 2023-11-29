package com.example.demoproject
import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.demoproject.databinding.ActivityStartScanBinding


open class StartScanActivity : AppCompatActivity() {
    lateinit var binding: ActivityStartScanBinding
    private val pic_id = 123
    private val CAMERA_PERMISSION_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityStartScanBinding.inflate(layoutInflater)
        initView()
        setContentView(binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initView() {
//        binding.ss.setOnClickListener {
//        startActivity(Intent(this,ResultActivity::class.java))
//        }
        binding.ss.setOnClickListener {
            showCustomDialog()
//            checkPermission(Manifest.permission.CAMERA,
//                CAMERA_PERMISSION_CODE)
//            val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(camera_intent, pic_id)
        }
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pic_id) {
            // BitMap is data structure of image file which store the image in memory
            val photo = data?.extras!!["data"] as Bitmap?
            Constant.bitmap=photo
            // Set the image in imageview for display
//            click_image_id.setImageBitmap(photo)
            Constant.value+=1;
            val intent = Intent(this, ResultActivity::class.java)
//            intent.putExtra("EXTRA_DATA", photo)
            startActivity( intent)
//            startActivity(Intent(this,ResultActivity::class.java))
        }
    }
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@StartScanActivity, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@StartScanActivity, arrayOf(permission), requestCode)
        } else {
                        val camera_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(camera_intent, pic_id)
//            Toast.makeText(this@StartScanActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this@StartScanActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@StartScanActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}