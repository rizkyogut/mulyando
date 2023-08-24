package com.rizkym.mulyando.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.rizkym.mulyando.R
import com.rizkym.mulyando.databinding.CardViewMainBinding
import com.rizkym.mulyando.model.Data

class MainAdapter(options: FirebaseRecyclerOptions<Data>, private val callback: DataCallback) : FirebaseRecyclerAdapter<Data, MainAdapter.ViewHolder>(options){

    inner class ViewHolder(private val binding: CardViewMainBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: Data) {
            binding.apply {
                name.text = data.userName
                place.text = data.place
                address.text = data.address
                root.setOnClickListener { callback.onDataClick(data) }
            }
        }
    }

    interface DataCallback {
        fun onDataClick(data: Data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.card_view_main, parent, false)
        val binding = CardViewMainBinding.bind(view)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Data) {
        holder.bind(model)
    }
}