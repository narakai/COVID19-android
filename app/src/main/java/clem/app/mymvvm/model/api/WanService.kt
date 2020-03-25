package clem.app.mymvvm.model.api

import retrofit2.http.*


/**
 * Created by luyao
 * on 2018/3/13 14:33
 */
interface WanService {

    companion object {
        const val BASE_URL = "https://github.com"
    }

    @GET("/CSSEGISandData/COVID-19/blob/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv")
    suspend fun getConfirmed(): String

    @GET("/CSSEGISandData/COVID-19/blob/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Deaths.csv")
    suspend fun getDeath(): String

    @GET("/CSSEGISandData/COVID-19/blob/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Recovered.csv")
    suspend fun getRecovered(): String

}