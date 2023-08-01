package com.rizkym.mulyando.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rizkym.mulyando.R
import com.rizkym.mulyando.databinding.FragmentProfileBinding
import com.rizkym.mulyando.model.Teknisi
import com.rizkym.mulyando.utils.setImageProfile

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val teknisiData = arguments?.getParcelable<Teknisi>("DATA_TEKNISI")

        if (teknisiData != null) {
            teknisiData.url.let { binding.imageView.setImageProfile(it) }
            binding.name.text = teknisiData.name
            binding.phoneNumber.text = teknisiData.phoneNumber
        }
    }
}