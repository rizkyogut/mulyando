package com.rizkym.mulyando

import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.rizkym.mulyando.bantuan.BantuanFragment
import com.rizkym.mulyando.databinding.ActivityMainBinding
import com.rizkym.mulyando.profile.ProfileActivity
import com.rizkym.mulyando.setting.SettingFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.appBarMain.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        drawerLayout = binding.drawerLayout

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        navView = binding.navigationView
        navView.setNavigationItemSelectedListener(this)

        val headerView = navView.getHeaderView(0)
        val profileImage = headerView.findViewById<ImageView>(R.id.nav_profile)
        val backgroundProfile = headerView.findViewById<ImageView>(R.id.background_image)

        profileImage.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_riwayat_pekerjaan -> {
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                return true
            }

            R.id.nav_bantuan -> {
                val bantuanFragment = BantuanFragment()
                show(bantuanFragment)
            }
            R.id.nav_setting -> {
                val settingFragment = SettingFragment()
                show(settingFragment)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.notificationMenu -> {
                Toast.makeText(applicationContext, "Hello", Toast.LENGTH_LONG).show()
                true
            }
            else -> true
        }
    }

    private fun show(fragment: Fragment) {

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val fragmentManager = supportFragmentManager

        fragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, fragment)
            .commit()

        drawerLayout.closeDrawer(GravityCompat.START)
    }
}