package com.laquysoft.polyarcore.api

import com.laquysoft.bernini.model.AssetModel
import retrofit2.Call
import javax.inject.Inject

/**
 * Created by joaobiriba on 22/12/2017.
 */
class PolyRestAPI @Inject constructor(private val polyService: PolyService) : PolyAPI {
    override fun getAsset(assetId: String, key: String): Call<AssetModel> {
        return polyService.getAsset(assetId, key)
    }
}