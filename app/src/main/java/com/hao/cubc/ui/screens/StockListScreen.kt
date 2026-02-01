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
    avgList: List<StockAvgPriceModel>
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

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
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
                            modifier = Modifier.fillMaxSize()
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
