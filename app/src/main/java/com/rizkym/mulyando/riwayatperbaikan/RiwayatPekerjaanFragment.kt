package com.rizkym.mulyando.riwayatperbaikan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.rizkym.mulyando.BuildConfig
import com.rizkym.mulyando.databinding.FragmentRiwayatPekerjaanBinding
import com.rizkym.mulyando.home.MainAdapter
import com.rizkym.mulyando.home.detail.DetailActivity
import com.rizkym.mulyando.model.Data

class RiwayatPekerjaanFragment : Fragment(), MainAdapter.DataCallback  {

    private var _binding: FragmentRiwayatPekerjaanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance(BuildConfig.URL_FIREBASE)
    private val myReference: DatabaseReference = database.reference.child("Data")

    private lateinit var adapter: MainAdapter
    private lateinit var query: Query

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRiwayatPekerjaanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvRiwayat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRiwayat.setHasFixedSize(true)

        query = myReference.orderByChild("status").equalTo("FINISH")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    binding.ivNoData.visibility = View.INVISIBLE
                } else {
                    binding.ivNoData.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })


        val options = FirebaseRecyclerOptions.Builder<Data>()
            .setQuery(query, Data::class.java)
            .build()

        adapter = MainAdapter(options, this@RiwayatPekerjaanFragment)
        binding.rvRiwayat.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        binding.rvRiwayat.recycledViewPool.clear()
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