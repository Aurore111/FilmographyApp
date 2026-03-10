package fr.isen.aurore.filmographyapp.api

import fr.isen.aurore.filmographyapp.FilmApi
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApi {

    @GET("/")
    suspend fun getMovie(
        @Query("t") title: String,
        @Query("apikey") apiKey: String
    ): FilmApi
}