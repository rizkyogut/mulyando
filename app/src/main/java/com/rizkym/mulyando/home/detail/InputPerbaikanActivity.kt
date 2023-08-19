package com.rizkym.mulyando.home.detail

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rizkym.mulyando.BuildConfig
import com.rizkym.mulyando.R
import com.rizkym.mulyando.databinding.ActivityInputPerbaikanBinding
import com.rizkym.mulyando.model.Perbaikan
import java.util.UUID

class InputPerbaikanActivity : AppCompatActivity() {


    private lateinit var binding: ActivityInputPerbaikanBinding

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.URL_FIREBASE)
    private val myReference: DatabaseReference = database.reference.child("Data")

    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = firebaseStorage.reference

    private var imageUri: Uri? = null
    private var childKey: String? = null
    private var perbaikan: Perbaikan? = null

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputPerbaikanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        childKey = intent.getStringExtra(EXTRA_ID)

        binding.ivAddImage.setOnClickListener {
            startImagePicker()
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
                        .into(binding.ivAddImage)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.input_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.done -> {
                addPerbaikan()
                true
            }

            else -> true
        }
    }

    private fun addPerbaikan() {
        //UUID
        val imageName = UUID.randomUUID().toString()
        val imageReference = storageReference.child("perbaikan").child(imageName)

        //Upload Photo
        imageUri?.let { uri ->
            imageReference.putFile(uri).addOnSuccessListener {
                Toast.makeText(applicationContext, "Image uploaded", Toast.LENGTH_SHORT).show()

                //downloadable url
                val myUploadedImageReference = storageReference.child("perbaikan").child(imageName)
                myUploadedImageReference.downloadUrl.addOnSuccessListener { url ->

                    val imageURL = url.toString()
                    addPerbaikanToDatabase(imageURL, imageName)
                }
            }.addOnFailureListener {
                Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun addPerbaikanToDatabase(imageURL: String, imageName: String) {
        val perbaikanInput: String = binding.textPerbaikan.text.toString()

        val user = FirebaseAuth.getInstance().currentUser

        user?.let {
            val uid = it.uid
            perbaikan = Perbaikan(uid, perbaikanInput, imageURL, imageName)
        }

        val reference = myReference.child("$childKey")
        reference.child("perbaikan").setValue(perbaikan).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    applicationContext, "Perbaikan has been added to the database",
                    Toast.LENGTH_SHORT
                ).show()

                showLoading(false)
            } else {
                Toast.makeText(
                    applicationContext, task.exception.toString(), Toast.LENGTH_SHORT
                ).show()
            }
        }

        //Update status
        val updates = HashMap<String, Any>()
        updates["status"] = "COMPLETED"

        reference.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    applicationContext, "Status Perbaikan has been updated",
                    Toast.LENGTH_SHORT
                ).show()
                showLoading(false)

                finish()
            } else {
                Toast.makeText(
                    applicationContext, task.exception.toString(), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}