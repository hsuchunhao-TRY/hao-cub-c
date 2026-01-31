package com.hao.cubc.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hao.cubc.data.model.StockAvgPriceModel
import com.hao.cubc.data.model.StockDayDetailModel
import com.hao.cubc.data.model.StockPeModel
import com.hao.cubc.data.repository.StockRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class StockViewModel(private val repository: StockRepository) : ViewModel() {

    private val TAG = "StockVerify"

    var stockData by mutableStateOf<Triple<List<StockPeModel>, List<StockAvgPriceModel>, List<StockDayDetailModel>>?>(null)
        private set

    // 輪詢開關
    private var isPolling = false
    private val POLLING_INTERVAL = 30000L

    fun startPolling() {
        if (isPolling) return // 避免重複啟動
        isPolling = true

        flow {
            while (true) {
                // 1. 抓取資料
                val result = repository.fetchAllThreeApis()
                // 2. 發射資料給下游
                emit(result)
                // 3. 等待一段時間再繼續下一次循環
                delay(POLLING_INTERVAL)
            }
        }
        .onEach { result -> // 💡 這裡拿到的是整個 Triple
            val (peList, avgList, dayList) = result

            // 💡 關鍵：必須把資料存進變數，UI 才會重畫！
            stockData = result

            Log.d("StockPolling", "========= 輪詢數據更新 =========")
            Log.d("StockPolling", "1. BWIBBU_ALL (本益比)  : ${peList.size} 筆")
            Log.d("StockPolling", "2. STOCK_DAY_AVG (均價) : ${avgList.size} 筆")
            Log.d("StockPolling", "3. STOCK_DAY_ALL (成交) : ${dayList.size} 筆")

            // 如果想確認內容，也可以各挑一筆印出來
            if (dayList.isNotEmpty()) {
                val topStock = dayList[0]
                Log.d("StockPolling", "即時快訊 -> ${topStock.Name}: 價格 ${topStock.ClosingPrice}, 漲跌 ${topStock.Change}")
            }
            Log.d("StockPolling", "================================")
        }
        .catch { e ->
            Log.e("StockPolling", "輪詢發生錯誤: ${e.message}")
        }
        .launchIn(viewModelScope) // 在 ViewModel 的生命週期內運行，銷毀時自動停止
    }
}

class StockViewModelFactory(private val repository: StockRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}