package com.laquysoft.polyarcore.api

import com.laquysoft.polyarcore.model.AssetModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by joaobiriba on 22/12/2017.
 */
interface PolyService {

    @GET("/v1/assets/{assetId}/")
    fun getAsset(@Path("assetId") assetId: String , @Query("key") key: String) : Call<AssetModel>
}