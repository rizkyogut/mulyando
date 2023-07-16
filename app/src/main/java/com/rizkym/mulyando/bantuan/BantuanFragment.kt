package com.rizkym.mulyando.bantuan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rizkym.mulyando.R

class BantuanFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.v("BantuanFragment","1")
        return inflater.inflate(R.layout.fragment_bantuan, container, false)
    }

}