package com.rizkym.mulyando.riwayatperbaikan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rizkym.mulyando.R

class RiwayatPekerjaanFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v("RiwayatPekerjaanFragment","1")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_riwayat_pekerjaan, container, false)
    }

}