package com.hao.cubc.ui

import MainApp
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hao.cubc.data.api.TwseApiService
import com.hao.cubc.data.repository.StockRepository
import com.hao.cubc.ui.theme.StockTheme
import com.hao.cubc.viewmodel.StockViewModel
import com.hao.cubc.viewmodel.StockViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl(TwseApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(TwseApiService::class.java)
        val repository = StockRepository(apiService)

        setContent {
            // 1. 建立一個狀態，預設跟隨系統
            var isDarkMode by remember { mutableStateOf(false) }

            StockTheme(darkTheme = isDarkMode) {
                val viewModel: StockViewModel = viewModel(
                    factory = StockViewModelFactory(repository)
                )

                // 呼叫 MainApp
                MainApp(
                    viewModel = viewModel,
                    isDarkMode = isDarkMode,
                    onThemeToggle = { isDarkMode = !isDarkMode }
                )
            }
        }
    }
}