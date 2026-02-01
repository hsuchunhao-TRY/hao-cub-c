package com.hao.cubc.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hao.cubc.ui.screens.StockMainScreen
import com.hao.cubc.viewmodel.StockViewModel

@Composable
fun MainApp(
    viewModel: StockViewModel,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit
) {
    val context = LocalContext.current
    val categoryManager = remember { CategoryManager(context) }

    // 💡 加入這段：確保進來時啟動輪詢
    LaunchedEffect(Unit) {
        viewModel.startPolling()
    }

    val data = viewModel.stockData

    if (data == null) {
        LoadingScreen()
    } else {
        StockMainScreen(
            stockData = data,
            isDarkMode = isDarkMode,
            onThemeToggle = onThemeToggle,
            categoryManager = categoryManager
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "正在獲取台股即時數據...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

class CategoryManager(context: Context) {
    // 儲存所有分類，Key 是分類名 (如 aiList), Value 是該分類的代碼 Set
    var dynamicCategories: Map<String, Set<String>> = emptyMap()
        private set

    init {
        val jsonString = context.assets.open("stock_groups.json")
            .bufferedReader().use { it.readText() }

        val type = object : TypeToken<Map<String, Any>>() {}.type
        val rawMap: Map<String, Any> = Gson().fromJson(jsonString, type)

        // 過濾掉註解 (如 _comment) 並轉換資料
        dynamicCategories = rawMap.filter { !it.key.startsWith("_") }
            .mapValues { (_, value) ->
                // 將 List<Map> 轉換成 Set<String> (代碼)
                val list = value as List<Map<String, String>>
                list.map { it["code"] ?: "" }.toSet()
            }
    }
}