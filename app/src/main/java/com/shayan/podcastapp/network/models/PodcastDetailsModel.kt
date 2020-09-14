package com.shayan.podcastapp.network.models

import com.squareup.moshi.Json
import java.io.Serializable

data class PodcastDetailsModel(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "type") val type: String,
    @field:Json(name = "image") val image: String,
    @field:Json(name = "thumbnail") val thumbnail: String,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "country") val country: String,
    @field:Json(name = "language") val language: String,
    @field:Json(name = "publisher") val publisher: String,
    @field:Json(name = "total_episodes") val total_episodes: Int,
    @field:Json(name = "episodes") val episodes: List<EpisodeModel>
) : Serializable