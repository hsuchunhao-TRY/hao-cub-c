package com.hao.cubc.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import com.hao.cubc.R


// ... 其他 import
import com.hao.cubc.data.api.TwseApiService
import com.hao.cubc.data.repository.StockRepository
import com.hao.cubc.viewmodel.StockViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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