package tech.yeswecode.week14

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import tech.yeswecode.week14.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 1
    var storageRef = Firebase.storage.getReference()
    private lateinit var binding: ActivityMainBinding
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ::onGalleryResult)

        requestPermissions(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ), REQUEST_CODE)

        binding.image.setOnLongClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            galleryLauncher.launch(intent)
            true
        }

        getImage("e81b25fe-c88a-4164-bda2-6c9cf2b703ec")

    }

    fun getImage(photoId: String) {
        storageRef.child("profile").child(photoId).downloadUrl.addOnSuccessListener {
            Glide.with(binding.image).load(it).into(binding.image)
        }
    }

    fun onGalleryResult(result: ActivityResult){
        if(result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            binding.image.setImageURI(uri)
            val filename = UUID.randomUUID().toString()
            storageRef.child("profile").child(filename).putFile(uri!!)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE) {
            var allGranted = true
            for (result in grantResults) {
                if(result == PackageManager.PERMISSION_DENIED) {
                    allGranted = false
                }
            }
        }
    }
}