package com.rizkym.mulyando.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rizkym.mulyando.BuildConfig
import com.rizkym.mulyando.MainActivity
import com.rizkym.mulyando.databinding.ActivityCreatedTeknisiBinding
import com.rizkym.mulyando.model.Teknisi
import java.util.UUID

class CreatedTeknisiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatedTeknisiBinding

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.URL_FIREBASE)
    private val myReference: DatabaseReference = database.reference.child("MyTeknisi")

    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = firebaseStorage.reference

    private var imageUri: Uri? = null
    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatedTeknisiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNumber = intent.getStringExtra("phoneNumber")!!
        binding.textPhone.setText(phoneNumber).toString()

        binding.photoProfile.setOnClickListener {
            startImagePicker()
        }

        binding.button.setOnClickListener {
            createTeknisiUser()
        }
    }

    private fun startImagePicker() {
        showLoading(true)

        ImagePicker.with(this)
            .cameraOnly()
            .crop()                   //Crop image(Optional), Check Customization for more option
            .compress(1024)   //Final image size will be less than 1 MB(Optional)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    imageUri = data?.data!!

                    // Use Uri object instead of File to avoid storage permissions
                    Glide.with(this)
                        .load(imageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.photoProfile)

                    binding.imageView3.visibility = View.GONE
                }

                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }

            showLoading(false)
        }

    private fun createTeknisiUser() {

        binding.button.isClickable = false
        showLoading(true)

        //UUID
        val imageName = UUID.randomUUID().toString()
        val imageReference = storageReference.child("images").child(imageName)

        //Upload Photo
        imageUri?.let { uri ->
            imageReference.putFile(uri).addOnSuccessListener {
                Toast.makeText(applicationContext, "Image uploaded", Toast.LENGTH_SHORT).show()

                //downloadable url
                val myUploadedImageReference = storageReference.child("images").child(imageName)
                myUploadedImageReference.downloadUrl.addOnSuccessListener { url ->

                    val imageURL = url.toString()
                    addTeknisiToDatabase(imageURL, imageName)

                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun addTeknisiToDatabase(imageURL: String, imageName: String) {
        val name: String = binding.textName.text.toString()

        val user = FirebaseAuth.getInstance().currentUser
        val teknisi = Teknisi(name, phoneNumber, imageURL, imageName)

        user?.let {
            myReference.child(it.uid).setValue(teknisi).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext, "The new Teknisi has been added to the database",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.button.isClickable = true
                    binding.progressBar.visibility = View.INVISIBLE

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        applicationContext, task.exception.toString(), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}