package com.shayan.podcastapp.network.models

import com.squareup.moshi.Json

data class PodcastListModel(
    @field:Json(name = "has_next") val has_next : Boolean,
    @field:Json(name = "podcasts") val podcasts : List<PodcastModel>,
    @field:Json(name = "page_number") val page_number : Int,
    @field:Json(name = "next_page_number") val next_page_number : Int
)