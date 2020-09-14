package com.shayan.podcastapp.network

import com.shayan.podcastapp.BuildConfig
import com.shayan.podcastapp.network.models.PodcastDetailsModel
import com.shayan.podcastapp.network.models.PodcastListModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://listen-api.listennotes.com/api/v2/"

//Json parsing
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

//Todo: Create a "apikey.properties" file in the root folder
//Todo: Create a key value pair like so: API_KEY="XXXXXXXXXXXXXXXXXXXX" (replace your ListenNote api key)
//Todo: Build the project
interface PodcastApiService {
    @Headers("X-ListenAPI-Key: " + BuildConfig.API_KEY)
    @GET("best_podcasts")
    fun getPodcasts(@Query("page") page :Int): Call<PodcastListModel>

    @Headers("X-ListenAPI-Key: " + BuildConfig.API_KEY)
    @GET("podcasts/{id}")
    fun getEpisodes(@Path("id") id:String): Call<PodcastDetailsModel>
}

object PodcastApi {
    val retrofitService : PodcastApiService by lazy { retrofit.create(PodcastApiService::class.java) }
}