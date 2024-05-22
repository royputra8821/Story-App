package com.royputra.submissionstory.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.royputra.submissionstory.R
import com.royputra.submissionstory.data.retrofit.response.ListStoryItem

class StoryAdapter(
    private val storyList: List<ListStoryItem>,
    private val listener: OnAdapterListener
) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.tvName)
        val photo: ImageView = itemView.findViewById(R.id.imgStory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.story_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = storyList[position]
        holder.name.text =item.name
        Glide.with(holder.itemView.context)
            .load(item.photoUrl)
            .error(R.drawable.ic_launcher_background)
            .into(holder.photo)
        holder.itemView.setOnClickListener{
            listener.onClick(item)
        }
    }

    interface OnAdapterListener {
        fun onClick(story: ListStoryItem)
    }

    override fun getItemCount(): Int {
        return storyList.size
    }
}