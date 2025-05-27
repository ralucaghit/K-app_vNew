package com.example.k_app_v1
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.util.Log

class ImaginiAdapter(private val imagini: List<String>) : RecyclerView.Adapter<ImaginiAdapter.ImagineViewHolder>() {

    class ImagineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagineView: ImageView = itemView.findViewById(R.id.imagineView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_imagine, parent, false)
        return ImagineViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagineViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(imagini[position])
            .into(holder.imagineView)
    }

    override fun getItemCount(): Int = imagini.size
}