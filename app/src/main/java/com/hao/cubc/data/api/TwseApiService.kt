package com.hao.cubc.data.api

import com.hao.cubc.data.model.StockAvgPriceModel
import com.hao.cubc.data.model.StockDayDetailModel
import com.hao.cubc.data.model.StockPeModel
import retrofit2.http.GET

interface TwseApiService {
    @GET("exchangeReport/BWIBBU_ALL")
    suspend fun getBwibbuAll(): List<StockPeModel>

    @GET("exchangeReport/STOCK_DAY_AVG_ALL")
    suspend fun getStockDayAvgAll(): List<StockAvgPriceModel>

    @GET("exchangeReport/STOCK_DAY_ALL")
    suspend fun getStockDayAll(): List<StockDayDetailModel> // 補上這行

    companion object {
        const val BASE_URL = "https://openapi.twse.com.tw/v1/"
    }
}
