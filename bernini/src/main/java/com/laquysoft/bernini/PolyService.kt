package com.laquysoft.bernini

import com.laquysoft.bernini.model.AssetModel
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.ResponseBody



/**
 * Created by joaobiriba on 22/12/2017.
 */
interface PolyService {

    @GET("/v1/assets/{assetId}/")
    fun getAsset(@Path("assetId") assetId: String) : Call<AssetModel>

    @GET("/{filePath}")
    fun downloadFile(@Path("filePath", encoded = true) filePath: String): Deferred<ResponseBody>
}