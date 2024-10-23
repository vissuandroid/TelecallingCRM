package com.pixl.crm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pixl.crm.response.LeaderboardItem
import com.telecalling.crm.R
import com.bumptech.glide.request.RequestOptions
class LeaderboardAdapter(private val items: List<LeaderboardItem>) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

//    private val images = listOf(
//        R.drawable.cardindicators,
//        R.drawable.cardindicator2,
//        R.drawable.cardindicator3
//    )

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.leaderboard_name)
        val score: TextView = view.findViewById(R.id.leaderboard_score)
        val image: ImageView = view.findViewById(R.id.leaderboard_image)
        val rankText: TextView = view.findViewById(R.id.rank_text)
//        val indicator: ImageView = view.findViewById(R.id.indicator1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.staff_name
        holder.score.text = item.lead_count.toString()

        val imageUrl = item.staff_photo
//        val imageRes = images[position % images.size] // Cycle through the images
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(holder.image)

        holder.rankText.text = (position + 1).toString()
//        val indicatorRes = images[position % images.size]
//        holder.indicator.setImageResource(indicatorRes)
    }

    override fun getItemCount(): Int = items.size
}