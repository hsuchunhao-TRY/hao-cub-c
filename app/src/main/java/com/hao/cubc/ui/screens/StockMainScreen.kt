package com.hao.cubc.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hao.cubc.data.model.StockAvgPriceModel
import com.hao.cubc.data.model.StockDayDetailModel
import com.hao.cubc.data.model.StockPeModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockMainScreen(
    stockData: Triple<List<StockPeModel>, List<StockAvgPriceModel>, List<StockDayDetailModel>>, // 💡 確保有名稱叫 stockData
    isDarkMode: Boolean,          // 新增這個參數
    onThemeToggle: () -> Unit      // 新增這個參數，對應 MainActivity 傳來的 Lambda
){
    // 狀態宣告：控制 BottomSheet 是否顯示
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Scaffold 是主佈局結構
    val (peList, avgList, detailList) = stockData
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("台股即時快訊") },
                actions = {
                    // --- 夜間模式切換按鈕 ---
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "切換主題"
                        )
                    }

                    // 1. 右上方加個 Filter Button
                    IconButton(onClick = { showSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "篩選排序"
                        )
                    }
                },
                // 設定 TopBar 顏色，這裡會自動適應夜間模式
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            StockListScreen(
                detailList = detailList,
                peList = peList,
                avgList = avgList
            )
        }
    }

    // 3. 彈出選單 (Filter Menu)
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            // 指定 Sheet 的背景色，也會自動適配夜間模式
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            // 彈出選單內的排版
            FilterMenuContent {
                showSheet = false // 點擊選項後關閉選單
            }
        }
    }
}

@Composable
fun FilterMenuContent(onOptionClick: () -> Unit) {
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
        val options = listOf("成交價 (高->低)", "漲跌幅 (高->低)", "成交金額 (高->低)", "成交股數 (高->低)")
        options.forEach { label ->
            OutlinedButton(
                onClick = onOptionClick,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(label)
            }
        }
    }
}
