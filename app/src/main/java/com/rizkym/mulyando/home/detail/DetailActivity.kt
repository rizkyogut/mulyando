package com.rizkym.mulyando.home.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rizkym.mulyando.BuildConfig
import com.rizkym.mulyando.R
import com.rizkym.mulyando.databinding.ActivityDetailBinding
import com.rizkym.mulyando.home.detail.InputPerbaikanActivity.Companion.EXTRA_ID
import com.rizkym.mulyando.model.Data
import com.rizkym.mulyando.utils.getDateTime

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityDetailBinding

    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0

    private lateinit var mMap: GoogleMap

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.URL_FIREBASE)
    private val myReference: DatabaseReference = database.reference.child("Data")

    private var data: Data? = null
    private var childKey: String? = null
    private var status: String? = null

    companion object {
        const val EXTRA_DATA = "extra_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        data = intent.getParcelableExtra(EXTRA_DATA)

        if (data != null) {

            latitude = data?.latitude
            longitude = data?.longitude
            status = data?.status

            binding.apply {
                tvPlace.text = data?.place
                tvAddress.text = data?.address
                tvName.text = data?.userName
                tvDiagnosis.text = data?.diagnosisMachine
                tvMachineName.text = data?.nameMachine
                tvPerbaikan.text = data?.perbaikan?.perbaikan
                tvDate.text = getDateTime(data?.timeCreated.toString())
                tvStatus.text = data?.status

                direction.setOnClickListener {
                    val uri = "geo:<${latitude}>,<${longitude}>?q=<${latitude}>,<${longitude}>"
                    val gmmIntentUri = Uri.parse(uri)
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

                    mapIntent.setPackage("com.google.android.apps.maps")
                    mapIntent.resolveActivity(packageManager)?.let {
                        startActivity(mapIntent)
                    }
                }

                buttonCall.setOnClickListener {
                    dialPhoneNumber(data?.phoneUser)
                }

                cekStatus(data)
            }

            val query = myReference.orderByChild("user_timeCreated").equalTo(data?.user_timeCreated)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        childKey = childSnapshot.key // This is the child ID
                        binding.tvNoKerusakan.text = childKey
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()

        myReference.child("$childKey").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Handle the updated data here
                val dataUpdate = snapshot.getValue(Data::class.java)
                // Update your UI or model with the new data

                status = dataUpdate?.status

                cekStatus(dataUpdate)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle any errors
            }
        })
    }

    private fun showDialog() {
        AlertDialog.Builder(this@DetailActivity)
            .setMessage("Are you sure already finish the task?")
            .setPositiveButton("Yes") { _, _ ->
                val updates = HashMap<String, Any>()
                updates["status"] = "FINISH"

                myReference.child("$childKey").updateChildren(updates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                applicationContext, "Status Perbaikan has been updated",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext, task.exception.toString(), Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun dialPhoneNumber(phoneUser: String?) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneUser")
        }
        startActivity(intent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val location = LatLng(latitude!!, longitude!!)

        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.addMarker(MarkerOptions().position(location))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun cekStatus(dataUpdate: Data?) {
        when (status) {
            "KERUSAKAN" -> {
                binding.tvStatus.text = status
                binding.buttonInput.setOnClickListener {
                    val intent =
                        Intent(this@DetailActivity, InputPerbaikanActivity::class.java)
                    intent.putExtra(EXTRA_ID, childKey)
                    startActivity(intent)
                }
            }

            "COMPLETED" -> {
                binding.tvStatus.text = status
                binding.tvPerbaikan.text = dataUpdate?.perbaikan?.perbaikan

                binding.buttonInput.apply {
                    setBackgroundColor(
                        ContextCompat.getColor(
                            applicationContext, R.color.md_theme_light_onPrimaryContainer
                        )
                    )

                    text = context.getString(R.string.perbaikan_selesai)

                    setOnClickListener {
                        showDialog()
                    }
                }
            }

            "FINISH" -> {
                binding.tvStatus.text = status
                binding.tvPerbaikan.text = dataUpdate?.perbaikan?.perbaikan
                binding.buttonInput.visibility = View.INVISIBLE
                binding.buttonCall.visibility = View.INVISIBLE
            }
        }
    }

}