package clem.app.mymvvm.model.repository

import clem.app.mymvvm.model.api.BaseRepository
import clem.app.mymvvm.model.api.WanRetrofitClient


/**
 * Created by luyao
 * on 2019/4/10 14:09
 */
class HomeRepository : BaseRepository() {

    suspend fun getConfirmed(): String {
        return apiCall { WanRetrofitClient.service.getConfirmed() }
    }

    suspend fun getDeath(): String {
        return apiCall { WanRetrofitClient.service.getDeath() }
    }

    suspend fun getRecovered(): String {
        return apiCall { WanRetrofitClient.service.getRecovered() }
    }
}