package com.example.firebase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var TAG = "MainActivity"
    private var imageUri: Uri? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("user_images");
        binding.ivImage.setOnClickListener {
            mGetContent.launch("image/*");
        }

        binding.btSave.setOnClickListener {
            saveInfo()
        }

    }

    //Get image extention
    fun GetFileExtention(uri: Uri?): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
    private fun saveInfo() {
        if (imageUri != null) {
            Log.d(TAG, "saveInfo: ")
            storageReference = storageReference.child(""+System.currentTimeMillis() +"." + GetFileExtention(imageUri))
            storageReference.putFile(imageUri!!)
                .addOnSuccessListener {
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        val url = uri.toString()
                        val userinfo = Info(binding.etName.text.toString(),url)
                        databaseReference.child("dfgreg").setValue(userinfo)
                        Log.d(TAG, "Successfully added")
                        Toast.makeText(
                            this,
                            "Successfully added",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    }
                }.addOnFailureListener { e ->

                    Log.d(TAG, "" + e.message)
                    Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    var mGetContent = registerForActivityResult(
        GetContent()
    ) { uri -> // Handle the returned Uri
        imageUri = uri
        binding.ivImage.setImageURI(imageUri)
    }

}