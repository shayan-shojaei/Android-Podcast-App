package com.shayan.podcastapp.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shayan.podcastapp.R
import com.shayan.podcastapp.adapters.EpisodesAdapter
import com.shayan.podcastapp.network.models.EpisodeModel
import com.shayan.podcastapp.network.PodcastApi
import com.shayan.podcastapp.network.models.PodcastDetailsModel
import com.shayan.podcastapp.network.models.PodcastModel
import kotlinx.android.synthetic.main.activity_podcast_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PodcastListActivity : AppCompatActivity(), EpisodesAdapter.Interaction {

    private lateinit var podcast: PodcastModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_podcast_list)
        podcast = intent.getSerializableExtra("podcast") as PodcastModel
        init()
        retrieveEpisodes()
    }

    // Views declaration
    private lateinit var image: ImageView
    private lateinit var title: TextView
    private lateinit var author: TextView
    private lateinit var description: TextView
    private lateinit var epCount: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var pb: ProgressBar

    // RecyclerView essentials declaration
    private lateinit var list: MutableList<EpisodeModel>
    private lateinit var adapter: EpisodesAdapter

    @SuppressLint("SetTextI18n")
    private fun init() {
        // View Binding
        image = plist_image
        title = plist_title
        author = plist_author
        description = plist_description
        epCount = plist_epcount
        recyclerView = plist_rv
        val gradient = plist_gradient
        pb = plist_pb

        // Shared Elements
        image.transitionName = "image"
        title.transitionName = "title"
        gradient.transitionName = "gradient"

        // Data Binding
        author.text = getString(R.string.by) + podcast.publisher
        title.text = HtmlCompat.fromHtml(podcast.title, HtmlCompat.FROM_HTML_MODE_COMPACT)
        description.text =
            HtmlCompat.fromHtml(podcast.description, HtmlCompat.FROM_HTML_MODE_COMPACT)
        epCount.text = podcast.total_episodes.toString() + getString(R.string.episodes)

        Glide.with(this)
            .asDrawable()
            .load(podcast.image)
            .centerCrop()
            .into(image)

        // RecyclerView essentials initialization
        adapter = EpisodesAdapter(this)
        list = ArrayList()
        recyclerView.adapter = adapter
    }

    private fun retrieveEpisodes() {
        setState(State.LOADING)
        PodcastApi.retrofitService.getEpisodes(podcast.id)
            .enqueue(object : Callback<PodcastDetailsModel> {
                override fun onFailure(call: Call<PodcastDetailsModel>, t: Throwable) {
                    Log.e("error", t.message.toString())
                    setState(State.ERROR)
                }

                override fun onResponse(
                    call: Call<PodcastDetailsModel>,
                    response: Response<PodcastDetailsModel>
                ) {
                    when (response.code()) {
                        200 -> {
                            val podcastDetails = response.body()!!
                            handleResponse(podcastDetails.episodes)
                            setState(State.LOADED)
                        }
                        else -> {
                            setState(State.ERROR)
                        }
                    }
                }
            })
    }

    private fun handleResponse(newList : List<EpisodeModel>){
        // Append new items to list
        // TODO: Setup lazy loading
        list.addAll(newList)

        adapter.submitList(list)
        adapter.notifyDataSetChanged()
    }
    private fun setState(state: State) {
        when (state) {
            State.LOADED -> {
                recyclerView.visibility = View.VISIBLE
                pb.visibility = View.GONE
            }
            State.LOADING -> {
                recyclerView.visibility = View.GONE
                pb.visibility = View.VISIBLE
            }
            State.ERROR -> {
                recyclerView.visibility = View.GONE
                pb.visibility = View.GONE
            }
        }
    }

    override fun onItemSelected(position: Int, item: EpisodeModel) {
        val intent = Intent(this, EpisodeActivity::class.java)
        intent.putExtra("episode", item)
        startActivity(intent)
    }

}

enum class State {
    LOADING, LOADED, ERROR
}