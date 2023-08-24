package com.rizkym.mulyando.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rizkym.mulyando.home.tabs.ProgressFragment
import com.rizkym.mulyando.home.tabs.KerusakanFragment

class SectionsPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = KerusakanFragment()
            1 -> fragment = ProgressFragment()
        }
        return fragment as Fragment
    }

}