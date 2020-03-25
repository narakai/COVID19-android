package clem.app.mymvvm.ui

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import clem.app.core.base.BaseViewModel
import clem.app.mymvvm.executeResponse
import clem.app.mymvvm.model.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * Created by luyao
 * on 2019/1/29 10:27
 */
class HomeViewModel : BaseViewModel() {

    private val repository by lazy { HomeRepository() }
    val confirmed: MutableLiveData<String> = MutableLiveData()
    val confirmedErrorLiveData: MutableLiveData<String> = MutableLiveData()
    val death: MutableLiveData<String> = MutableLiveData()
    val recovered: MutableLiveData<String> = MutableLiveData()

//    fun getConfirmed() {
//        launch {
//            val result = withContext(Dispatchers.IO) { repository.getConfirmed() }
//            executeResponse(result, { confirmed.value = result.data }, {})
//        }
//    }

    fun getConfirmed() {
        launchOnUITryCatch({
            val result = withContext(Dispatchers.IO) { repository.getConfirmed() }
            executeResponse(result, { confirmed.value = result }, {})
        }, {
            confirmedErrorLiveData.value = it.message
        }, {}, false)
    }

    fun getDeath() {
        launch {
            val result = withContext(Dispatchers.IO) { repository.getDeath() }
            executeResponse(result, { death.value = result }, {})
        }
    }

    fun getRecovered() {
        launch {
            val result = withContext(Dispatchers.IO) { repository.getRecovered() }
            executeResponse(result, { recovered.value = result }, {})
        }
    }

    //may many func
}