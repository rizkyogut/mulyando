package com.rizkym.mulyando

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rizkym.mulyando.auth.CreatedTeknisiActivity
import com.rizkym.mulyando.bantuan.BantuanFragment
import com.rizkym.mulyando.databinding.ActivityMainBinding
import com.rizkym.mulyando.home.HomeFragment
import com.rizkym.mulyando.home.tabs.ProgressFragment
import com.rizkym.mulyando.model.Teknisi
import com.rizkym.mulyando.profile.ProfileFragment
import com.rizkym.mulyando.riwayatperbaikan.RiwayatPekerjaanFragment
import com.rizkym.mulyando.setting.SettingFragment
import com.rizkym.mulyando.utils.setImageBackground
import com.rizkym.mulyando.utils.setImageProfile

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.URL_FIREBASE)
    private val myReference: DatabaseReference = database.reference.child("MyTeknisi")

    private var phoneNumber: String? = null
    private var title : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.appBarMain.toolbar
        setSupportActionBar(toolbar)

        phoneNumber = intent.getStringExtra("phoneNumber")

        drawerLayout = binding.drawerLayout

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        navView = binding.navigationView
        navView.setNavigationItemSelectedListener(this)

        checkData()
    }

    private fun checkData() {
        val user = FirebaseAuth.getInstance().currentUser

        user?.let {

            val uid = it.uid

            myReference.child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val teknisi = dataSnapshot.getValue(Teknisi::class.java)
                            viewData(teknisi)
                        } else {
                            val intent =
                                Intent(this@MainActivity, CreatedTeknisiActivity::class.java)
                            intent.putExtra("phoneNumber", phoneNumber)
                            startActivity(intent)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", error.toException())
                    }
                })
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val homeFragment = HomeFragment()
                title = resources.getString(R.string.app_name)
                show(homeFragment, title)
            }

            R.id.nav_riwayat_pekerjaan -> {
                val riwayatFragment = RiwayatPekerjaanFragment()
                title = resources.getString(R.string.riwayat_pekerjaan)
                show(riwayatFragment, title)
            }

            R.id.nav_bantuan -> {
                val bantuanFragment = BantuanFragment()
                title = resources.getString(R.string.bantuan)
                show(bantuanFragment, title)
            }

            R.id.nav_setting -> {
                val settingFragment = SettingFragment()
                title = resources.getString(R.string.setting)
                show(settingFragment, title)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun show(fragment: Fragment, title: String?) {

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val fragmentManager = supportFragmentManager
        supportActionBar?.title = title

        fragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, fragment)
            .commit()

        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun viewData(teknisi: Teknisi?) {
        val headerView = navView.getHeaderView(0)
        val profileImage = headerView.findViewById<ImageView>(R.id.nav_profile)
        val backgroundProfile = headerView.findViewById<ImageView>(R.id.background_image)
        val headerMenu = navView.menu.findItem(R.id.main_item)

        teknisi?.url.let { profileImage.setImageProfile(it) }
        teknisi?.url.let { backgroundProfile.setImageBackground(it) }
        headerMenu.title = teknisi?.name

        profileImage.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("DATA_TEKNISI", teknisi)

            val profileFragment = ProfileFragment()
            profileFragment.arguments = bundle

            title = resources.getString(R.string.profile)
            show(profileFragment, title)
        }
    }

}