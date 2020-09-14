package com.shayan.podcastapp.adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shayan.podcastapp.R
import com.shayan.podcastapp.network.models.EpisodeModel
import kotlinx.android.synthetic.main.layout_episode_item.view.*

class EpisodesAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    //DiffUtil Setup
    private val diffCallback = object : DiffUtil.ItemCallback<EpisodeModel>() {

        override fun areItemsTheSame(oldItem: EpisodeModel, newItem: EpisodeModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: EpisodeModel,
            newItem: EpisodeModel
        ): Boolean {
            return oldItem.contentEquals(newItem)
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return EpisodesHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_episode_item,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EpisodesHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<EpisodeModel>) {
        differ.submitList(list)
    }

    class EpisodesHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: EpisodeModel) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            itemView.ei_title.text = HtmlCompat.fromHtml(item.title, HtmlCompat.FROM_HTML_MODE_COMPACT)
            itemView.ei_length.text = formatDuration(item.audio_length_sec)
        }

        private fun formatDuration(seconds: Int): String = if (seconds < 60) {
            seconds.toString()
        } else {
            DateUtils.formatElapsedTime(seconds.toLong())
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: EpisodeModel)
    }



}
