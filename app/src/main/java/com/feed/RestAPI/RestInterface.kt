package com.mvpproject.Util

import com.feed.Model.RootModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface RestInterface {

    //Search by date API
    @GET("search_by_date")
    fun searchData(@Query("tags") tags: String, @Query("page") page: Int): Call<RootModel>


}