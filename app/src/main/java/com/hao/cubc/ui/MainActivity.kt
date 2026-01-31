package com.hao.cubc.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hao.cubc.data.api.TwseApiService
import com.hao.cubc.data.repository.StockRepository
import com.hao.cubc.ui.screens.StockMainScreen
import com.hao.cubc.ui.theme.StockTheme
import com.hao.cubc.viewmodel.StockViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // 1. 建立一個狀態，預設跟隨系統
            var isDarkMode by remember { mutableStateOf(false) }

            StockTheme {
                StockMainScreen(
                    isDarkMode = isDarkMode, // 傳入目前的狀態
                    onThemeToggle = {        // 傳入「點擊後要做什麼」的代碼塊
                        isDarkMode = !isDarkMode
                    }
                )
            }
        }

        // 1. 初始化 Retrofit 與 Service
        val retrofit = Retrofit.Builder()
            .baseUrl(TwseApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(TwseApiService::class.java)

        // 2. 初始化 Repository 與 ViewModel
        val repository = StockRepository(apiService)
        val viewModel = StockViewModel(repository)

        // 3. 執行驗證
//        viewModel.finalVerify()
        viewModel.startPolling()
    }
}