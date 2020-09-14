package com.shayan.podcastapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shayan.podcastapp.R
import com.shayan.podcastapp.network.models.PodcastModel

class PodcastAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //DiffUtil Setup
    private val diffCallback = object : DiffUtil.ItemCallback<PodcastModel>() {

        override fun areItemsTheSame(oldItem: PodcastModel, newItem: PodcastModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PodcastModel,
            newItem: PodcastModel
        ): Boolean {
            return oldItem.contentEquals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return PodcastHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_podcast_item,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PodcastHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<PodcastModel>) {
        differ.submitList(list)
    }

    class PodcastHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: PodcastModel) = with(itemView) {
            val titleTv = itemView.findViewById<TextView>(R.id.pi_title)
            val imageIv = itemView.findViewById<ImageView>(R.id.pi_image)
            val gradientIv = itemView.findViewById<ImageView>(R.id.pi_gradient)
            titleTv.text = HtmlCompat.fromHtml(item.title, HtmlCompat.FROM_HTML_MODE_COMPACT)
            Glide.with(context)
                .asDrawable()
                .load(Uri.parse(item.thumbnail))
                .centerCrop()
                .into(imageIv)


            itemView.findViewById<View>(R.id.pi_card).setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item, titleTv, imageIv, gradientIv)
            }
        }
    }

    interface Interaction {
        fun onItemSelected(
            position: Int,
            item: PodcastModel,
            tv: TextView,
            iv: ImageView,
            gradient: ImageView
        )
    }
}