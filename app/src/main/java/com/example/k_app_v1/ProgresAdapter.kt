package com.example.k_app_v1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProgresAdapter(private val listaExercitii: List<ProgresExercitiu>) : RecyclerView.Adapter<ProgresAdapter.ExercitiuViewHolder>() {

    class ExercitiuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.exercitiuImage)
        val progresText: TextView = itemView.findViewById(R.id.progresText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercitiuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_progres, parent, false)
        return ExercitiuViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExercitiuViewHolder, position: Int) {
        val exercitiu = listaExercitii[position]
        // Pune aici resursa imaginei, de exemplu: R.drawable.adunare
        holder.image.setImageResource(exercitiu.imagineResursa)
        holder.progresText.text = "${exercitiu.procent}%"
    }

    override fun getItemCount() = listaExercitii.size
}

data class ProgresExercitiu(
    val nume: String,
    val procent: Int,
    val imagineResursa: Int // exemplu: R.drawable.adunare
)