package com.shayan.podcastapp.network.models

import com.squareup.moshi.Json
import java.io.Serializable

data class PodcastModel(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "type") val type: String,
    @field:Json(name = "image") val image: String,
    @field:Json(name = "thumbnail") val thumbnail: String,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "country") val country: String,
    @field:Json(name = "language") val language: String,
    @field:Json(name = "publisher") val publisher: String,
    @field:Json(name = "total_episodes") val total_episodes: Int
) : Serializable{
    override fun equals(other: Any?) : Boolean{
        if (other is PodcastModel) {
            if (id != other.id) return false
            return true
        }
        return false
    }
    fun contentEquals(other: Any?) : Boolean{
        if (other is PodcastModel) {
            if (id != other.id) return false
            if (title != other.title) return false
            if (description != other.description) return false
            if (image != other.image) return false
            if (thumbnail != other.thumbnail) return false
            return true
        }
        return false
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + thumbnail.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + language.hashCode()
        result = 31 * result + publisher.hashCode()
        result = 31 * result + total_episodes
        return result
    }
}