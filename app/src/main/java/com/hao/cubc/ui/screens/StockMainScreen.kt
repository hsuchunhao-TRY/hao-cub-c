package com.hao.cubc.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hao.cubc.data.model.StockAvgPriceModel
import com.hao.cubc.data.model.StockDayDetailModel
import com.hao.cubc.data.model.StockPeModel
import com.hao.cubc.utils.generateEtfList
import com.hao.cubc.utils.searchStocks
import com.hao.cubc.utils.sortByCode

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
    onThemeToggle: () -> Unit
){
    val (peList, avgList, detailList) = stockData

    // 💡 1. 統一狀態定義在最上方
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var searchQuery by remember { mutableStateOf("") }
    var currentOption by remember { mutableStateOf(StockFilterOption.CODE_ASC) }

    // 💡 2. 核心管線：這裡只定義一次
    val displayList = remember(detailList, searchQuery, currentOption) {
        if (searchQuery.isNotEmpty()) {
            detailList.filter {
                it.Code.contains(searchQuery, ignoreCase = true) ||
                        it.Name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            when (currentOption) {
                StockFilterOption.CODE_DESC -> detailList.sortedByDescending { it.Code }
                StockFilterOption.CODE_ASC -> detailList.sortedBy { it.Code }
                StockFilterOption.ETF_ONLY -> {
                    detailList.filter { it.Code.startsWith("00") || it.Code.startsWith("01") }
                        .sortedBy { it.Code }
                }
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
            FilterMenuContent(onOptionSelected = { selected ->
                currentOption = selected
                searchQuery = "" // 💡 點選選單時清空搜尋，確保篩選能生效
                showSheet = false
            })
        }
    }
}

@Composable
fun FilterMenuContent(onOptionSelected: (StockFilterOption) -> Unit) {
    val options = listOf(
        "依股票代號降序" to StockFilterOption.CODE_DESC,
        "依股票代號升序" to StockFilterOption.CODE_ASC,
        "ETF" to StockFilterOption.ETF_ONLY
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp, top = 8.dp)
    ) {
        Text(
            "排序與篩選",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 模擬幾個按鈕
        options.forEach { (label, option) ->
            OutlinedButton(
                onClick = { onOptionSelected(option) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(label)
            }
        }
    }
}
