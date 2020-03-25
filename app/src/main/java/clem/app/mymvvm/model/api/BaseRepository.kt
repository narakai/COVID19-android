package clem.app.mymvvm.model.api

import clem.app.mymvvm.model.bean.WanResponse

/**
 * Created by luyao
 * on 2019/4/10 9:41
 */
open class BaseRepository {

    suspend fun <String> apiCall(call: suspend () -> String): String {
        return call.invoke()
    }

}