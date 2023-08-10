package com.rizkym.mulyando.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rizkym.mulyando.BuildConfig
import com.rizkym.mulyando.databinding.FragmentHomeBinding
import com.rizkym.mulyando.home.detail.DetailActivity
import com.rizkym.mulyando.model.Data

class HomeFragment : Fragment(), MainAdapter.DataCallback {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.URL_FIREBASE)
    private val myReference: DatabaseReference = database.reference.child("Data")

    private lateinit var adapter: MainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvHome.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHome.setHasFixedSize(true)

        val query = myReference.orderByChild("status").equalTo("KERUSAKAN")
        val options = FirebaseRecyclerOptions.Builder<Data>()
            .setQuery(query, Data::class.java)
            .build()

        adapter = MainAdapter(options, this)
        binding.rvHome.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        binding.rvHome.recycledViewPool.clear()
        adapter.startListening()
    }

    override fun onDestroy() {
        adapter.stopListening()
        super.onDestroy()
    }

    override fun onDataClick(data: Data) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DATA, data)
        startActivity(intent)
    }
}