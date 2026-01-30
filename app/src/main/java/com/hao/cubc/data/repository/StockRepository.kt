package com.hao.cubc.data.repository

import com.hao.cubc.data.api.TwseApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class StockRepository(private val apiService: TwseApiService) {

    suspend fun fetchAllThreeApis() = withContext(Dispatchers.IO) {
        // 使用 async 達到真正的「平行抓取」
        val peDef = async { apiService.getBwibbuAll() }
        val avgDef = async { apiService.getStockDayAvgAll() }
        val dayDef = async { apiService.getStockDayAll() }

        // 回傳一個三元組
        Triple(peDef.await(), avgDef.await(), dayDef.await())
    }
}