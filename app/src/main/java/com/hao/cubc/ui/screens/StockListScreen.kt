package com.hao.cubc.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.hao.cubc.data.model.StockAvgPriceModel
import com.hao.cubc.data.model.StockDayDetailModel
import com.hao.cubc.data.model.StockPeModel
import kotlinx.coroutines.delay

@Composable
fun StockListScreen(
    displayList: List<StockDayDetailModel>,
    detailList: List<StockDayDetailModel>,
    peList: List<StockPeModel>,
    avgList: List<StockAvgPriceModel>,
    favoriteList: Set<String>,
    inventoryList: Set<String>,
    onFavoriteToggle: (String) -> Unit,
    onInventoryToggle: (String) -> Unit
) {
    // 這裡我們直接使用傳入的 displayList 進行 LazyColumn 繪製
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 💡 關鍵：對 displayList 進行迭代
        items(displayList, key = { it.Code }) { detailItem ->

            // --- 你原本的卡片狀態與邏輯 ---
            var isFlipped by remember { mutableStateOf(false) }

            // 從傳入的輔助清單中尋找對應代碼的資料
            val peItem = peList.find { it.Code == detailItem.Code }
            val avgItem = avgList.find { it.Code == detailItem.Code }

            LaunchedEffect(isFlipped) {
                if (isFlipped) {
                    delay(5000)
                    isFlipped = false
                }
            }

            val rotation by animateFloatAsState(
                targetValue = if (isFlipped) 180f else 0f,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                label = "CardFlip"
            )

            // 1. 取得漲跌停狀態
            val status = getPriceStatus(detailItem)

            // 2. 定義顏色
            val limitUpColor = Color(0xFFFF1744)   // 強力紅
            val limitDownColor = Color(0xFF00C853) // 強力綠

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .then(
                        // 💡 根據狀態套用邊框
                        when (status) {
                            PriceStatus.LIMIT_UP -> Modifier.border(3.dp, limitUpColor, RoundedCornerShape(12.dp))
                            PriceStatus.LIMIT_DOWN -> Modifier.border(3.dp, limitDownColor, RoundedCornerShape(12.dp))
                            else -> Modifier // 平常沒事就不加邊框
                        }
                    )
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
                        val stockCode = detailItem.Code
                        StockFrontContent(
                            detail = detailItem,
                            pe = peItem,
                            avg = avgItem,
                            modifier = Modifier.fillMaxSize(),
                            // 使用從參數傳進來的狀態與回調
                            isFavorite = stockCode in favoriteList,
                            isInventory = stockCode in inventoryList,
                            onFavoriteClick = { onFavoriteToggle(stockCode) },
                            onInventoryClick = { onInventoryToggle(stockCode) }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize().graphicsLayer { rotationY = 180f },
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

enum class PriceStatus { NORMAL, LIMIT_UP, LIMIT_DOWN }

fun getPriceStatus(detail: StockDayDetailModel): PriceStatus {
    val close = detail.ClosingPrice.toDoubleOrNull() ?: 0.0
    val change = detail.Change.toDoubleOrNull() ?: 0.0
    val lastClose = close - change

    if (lastClose <= 0) return PriceStatus.NORMAL

    val percent = (change / lastClose) * 100

    return when {
        percent >= 9.7 -> PriceStatus.LIMIT_UP    // 漲停
        percent <= -9.7 -> PriceStatus.LIMIT_DOWN // 跌停
        else -> PriceStatus.NORMAL
    }
}