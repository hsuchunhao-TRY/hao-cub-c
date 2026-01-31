package com.hao.cubc.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hao.cubc.data.model.StockAvgPriceModel
import com.hao.cubc.data.model.StockDayDetailModel
import com.hao.cubc.data.model.StockPeModel
import kotlinx.coroutines.delay

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
            var isFlipped by remember { mutableStateOf(false) }

            val peItem = peList.find { it.Code == detailItem.Code }
            val avgItem = avgList.find { it.Code == detailItem.Code }

            // 自動翻轉回來的功能 (LaunchedEffect)
            LaunchedEffect(isFlipped) {
                if (isFlipped) {
                    delay(5000) // ⏳ 設定停留時間，例如 5 秒
                    isFlipped = false // 自動翻轉回正面
                }
            }

            // 動態計算翻轉角度
            val rotation by animateFloatAsState(
                targetValue = if (isFlipped) 180f else 0f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                label = "CardFlip"
            )

            // 這裡定義 Card 的統一高度
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // 固定高度確保正反面大小一致
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                    }
                    .clickable { isFlipped = !isFlipped },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (rotation <= 90f) {
                        // 正面
                        StockFrontContent(
                            detail = detailItem,
                            pe = peItem,
                            avg = avgItem,
                            modifier = Modifier.fillMaxSize() // 填滿 Card
                        )
                    } else {
                        // 背面
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { rotationY = 180f }, // 修正文字鏡像
                            contentAlignment = Alignment.Center
                        ) {
                            StockBackContent(detailItem, peItem)
                        }
                    }
                }
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