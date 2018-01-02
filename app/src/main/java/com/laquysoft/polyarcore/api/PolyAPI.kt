package com.laquysoft.polyarcore.api

import com.laquysoft.bernini.model.AssetModel
import retrofit2.Call

/**
 * Created by joaobiriba on 22/12/2017.
 */
interface PolyAPI {
    fun getAsset(assetId: String, key: String): Call<AssetModel>
}