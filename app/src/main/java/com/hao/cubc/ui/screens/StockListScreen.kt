package com.hao.cubc.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hao.cubc.data.model.StockAvgPriceModel
import com.hao.cubc.data.model.StockDayDetailModel
import com.hao.cubc.data.model.StockPeModel
import com.hao.cubc.ui.screens.StockFrontContent
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun StockListScreen(
    detailList: List<StockDayDetailModel>,
    peList: List<StockPeModel>,
    avgList: List<StockAvgPriceModel>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(detailList) { detailItem ->
            // --- 1. 這裡為每一列宣告獨立的狀態 ---
            var isLocalDialogOpen by remember { mutableStateOf(false) }

            val peItem = peList.find { it.Code == detailItem.Code }
            val avgItem = avgList.find { it.Code == detailItem.Code }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        Log.d("CLICK", "點擊了 ${detailItem.Name}")
                        isLocalDialogOpen = true
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                StockFrontContent(
                    detail = detailItem,
                    pe = peItem,
                    avg = avgItem
                )
            }

            // --- 2. 對話框也放在 items 內部 ---
            if (isLocalDialogOpen) {
                StockDetailAlertDialog(
                    pe = peItem,
                    onDismiss = { isLocalDialogOpen = false }
                )
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
fun PreviewStockFrontContent() {
    // 這裡就是模擬 call 的動作
    StockFrontContent(
        detail = StockDayDetailModel(
            Code = "2330", Name = "台積電", ClosingPrice = "1030",
            Change = "+5.0", OpeningPrice = "1025", HighestPrice = "1035",
            LowestPrice = "1020", Transaction = "45,123",
            TradeVolume = "25,432,000", TradeValue = "26.2B", Date = "20240520"
        ),
        pe = null, // 模擬：如果 PE 資料還沒回來或找不到
        avg = StockAvgPriceModel(
            Date = "20240520", Code = "2330", Name = "台積電",
            ClosingPrice = "1030", MonthlyAveragePrice = "1015.5"
        )
    )
}