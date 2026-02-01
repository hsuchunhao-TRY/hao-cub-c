package com.hao.cubc.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hao.cubc.data.model.StockAvgPriceModel
import com.hao.cubc.data.model.StockDayDetailModel
import com.hao.cubc.data.model.StockPeModel
import com.hao.cubc.ui.CategoryManager

enum class StockFilterOption {
    CODE_DESC,  // 依股票代號降序
    CODE_ASC,   // 依股票代號升序
    ETF_ONLY    // ETF
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockMainScreen(
    stockData: Triple<List<StockPeModel>, List<StockAvgPriceModel>, List<StockDayDetailModel>>,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    categoryManager: CategoryManager
){
    val (peList, avgList, detailList) = stockData

    // 💡 1. 統一狀態定義在最上方
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var searchQuery by remember { mutableStateOf("") }
    var currentOption by remember { mutableStateOf(StockFilterOption.CODE_ASC) }
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }

    // 💡 2. 核心管線：這裡只定義一次
    val displayList = remember(detailList, searchQuery, currentOption, selectedCategoryName) {

        if (searchQuery.isNotEmpty()) {
            // 優先權 1：搜尋框有字時，顯示搜尋結果
            detailList.filter {
                it.Code.contains(searchQuery, ignoreCase = true) ||
                        it.Name.contains(searchQuery, ignoreCase = true)
            }
        } else if (!selectedCategoryName.isNullOrEmpty()) {
            // 💡 優先權 2：檢查 JSON 分類
            // 從 Manager 撈出對應分類的代碼集合 (Set)
            val targetCodes = categoryManager.dynamicCategories[selectedCategoryName] ?: emptySet()

            // 只留下代碼在該集合中的股票
            detailList.filter { it.Code in targetCodes }
        } else {
            // 優先權 3：一般的排序或 ETF 篩選
            when (currentOption) {
                StockFilterOption.CODE_DESC -> detailList.sortedByDescending { it.Code }
                StockFilterOption.ETF_ONLY -> detailList.filter { it.Code.startsWith("00") || it.Code.startsWith("01") }
                else -> detailList.sortedBy { it.Code }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("台股即時快訊") },
                actions = {
                    IconButton(onClick = onThemeToggle) {
                        Icon(if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode, "主題")
                    }
                    IconButton(onClick = { showSheet = true }) {
                        Icon(Icons.Default.FilterList, "篩選")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 🔍 搜尋框
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    // 💡 如果你希望搜尋時自動解除 ETF 模式，可以加這行：
                    // if(it.isNotEmpty()) currentOption = StockFilterOption.CODE_ASC
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("搜尋代碼或名稱") },
                singleLine = true
            )

            // 💡 顯示清單
            StockListScreen(
                displayList = displayList,
                detailList = detailList,
                peList = peList,
                avgList = avgList
            )
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            FilterMenuContent(
                categoryManager = categoryManager, // 傳入你初始化的 Manager
                onSortSelected = { option ->
                    currentOption = option
                    selectedCategoryName = null // 清除分類過濾
                    showSheet = false
                },
                onCategorySelected = { categoryName ->
                    selectedCategoryName = categoryName // 設定 JSON 分類
                    showSheet = false
                }
            )
        }
    }
}

@Composable
fun FilterMenuContent(
    categoryManager: CategoryManager, // 💡 傳入你的 Manager
    onSortSelected: (StockFilterOption) -> Unit, // 處理原本的升降序
    onCategorySelected: (String) -> Unit // 💡 處理 JSON 動態分類 (傳入分類名稱)
) {
    // 1. 固定排序選項
    val sortOptions = listOf(
        "依股票代號降序" to StockFilterOption.CODE_DESC,
        "依股票代號升序" to StockFilterOption.CODE_ASC,
        "ETF 清單" to StockFilterOption.ETF_ONLY
    )

    // 2. 從 JSON 取得的所有動態 Key (AI 概念股, 海運股...)
    val dynamicKeys = categoryManager.dynamicCategories.keys.toList()

    LazyColumn( // 使用 LazyColumn 避免分類太多超出螢幕
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp, top = 8.dp)
    ) {
        item {
            Text("基本排序", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
        }

        // 渲染固定排序按鈕
        sortOptions.forEach { (label, option) ->
            item {
                OutlinedButton(
                    onClick = { onSortSelected(option) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) { Text(label) }
            }
        }

        // 在 FilterMenuContent 的 LazyColumn 裡面
        item {
            TextButton(
                onClick = {
                    onSortSelected(StockFilterOption.CODE_ASC) // 重設為預設排序
                    onCategorySelected("") // 傳入空字串或特定訊號來清除 JSON 分類
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.ClearAll, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("清除所有篩選 (顯示全部)")
            }
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Text("主題分類 (JSON)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
        }

        // 💡 關鍵：動態產生來自 JSON 的分類按鈕
        dynamicKeys.forEach { categoryName ->
            item {
                Button( // 用不同的按鈕樣式區分
                    onClick = { onCategorySelected(categoryName) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(categoryName)
                }
            }
        }
    }
}