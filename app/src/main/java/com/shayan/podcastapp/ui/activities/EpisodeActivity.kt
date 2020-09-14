package com.shayan.podcastapp.ui.activities

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.format.DateUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.shayan.podcastapp.R
import com.shayan.podcastapp.network.models.EpisodeModel
import kotlinx.android.synthetic.main.activity_episode.*

class EpisodeActivity : AppCompatActivity() {

    private lateinit var episode : EpisodeModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode)
        episode = intent.extras!!.getSerializable("episode") as EpisodeModel
        init()
    }

    private fun init(){
        // Data Binding
        episode_title.text = HtmlCompat.fromHtml(episode.title,HtmlCompat.FROM_HTML_MODE_COMPACT)
        episode_description.text = HtmlCompat.fromHtml(episode.description,HtmlCompat.FROM_HTML_MODE_COMPACT)
        episode_length.text = formatDuration(episode.audio_length_sec)
        Glide.with(this)
            .asDrawable()
            .load(episode.image)
            .centerCrop()
            .into(episode_image)


        if (episode.maybe_audio_invalid){
            disableDownloadButton()
        }else{
            episode_download.setOnClickListener {

            }
        }
    }

    private fun disableDownloadButton(){
        episode_download.alpha = 0.4f
        episode_download.isEnabled = false
    }
    private fun downloadButtonClickHandler(){
        //Check WRITE_STORAGE Permission and download audio if granted
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                downloadEpisode()
                Toast.makeText(this,getString(R.string.download_started),Toast.LENGTH_SHORT).show()
                this.finish()
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),4444)
            }
        }
    }

    private fun formatDuration(seconds: Int): String = if (seconds < 60) {
        seconds.toString()
    } else {
        DateUtils.formatElapsedTime(seconds.toLong())
    }

    private fun downloadEpisode() {

        val mgr = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadUri = Uri.parse(episode.audio)
        val request = DownloadManager.Request(
            downloadUri
        )

        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or
                    DownloadManager.Request.NETWORK_MOBILE
        )
            .setAllowedOverRoaming(false)
            .setTitle(episode.title)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PODCASTS,
                "/${episode.title}.mp3"
            )

        mgr.enqueue(request)
    }
}