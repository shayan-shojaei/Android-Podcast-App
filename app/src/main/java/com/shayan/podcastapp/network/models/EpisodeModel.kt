package com.shayan.podcastapp.network.models

import com.squareup.moshi.Json
import java.io.Serializable

data class EpisodeModel(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "audio") val audio: String,
    @field:Json(name = "image") val image: String,
    @field:Json(name = "thumbnail") val thumbnail: String,
    @field:Json(name = "title") val title: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "audio_length_sec") val audio_length_sec: Int,
    @field:Json(name = "maybe_audio_invalid") val maybe_audio_invalid: Boolean
) : Serializable{
    override fun equals(other: Any?) : Boolean{
        if (other is EpisodeModel) {
            if (id != other.id) return false
            return true
        }
        return false
    }
    fun contentEquals(other: Any?) : Boolean{
        if (other is EpisodeModel) {
            if (id != other.id) return false
            if (title != other.title) return false
            if (description != other.description) return false
            if (audio_length_sec != other.audio_length_sec) return false
            if (image != other.image) return false
            if (thumbnail != other.thumbnail) return false
            if (audio != other.audio) return false
            return true
        }
        return false
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + audio.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + thumbnail.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + audio_length_sec
        result = 31 * result + maybe_audio_invalid.hashCode()
        return result
    }
}