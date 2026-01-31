package com.hao.cubc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hao.cubc.data.model.StockAvgPriceModel
import com.hao.cubc.data.model.StockDayDetailModel
import com.hao.cubc.data.model.StockPeModel

@Composable
fun StockFrontContent(
    pe: StockPeModel?,
    avg: StockAvgPriceModel?,
    detail: StockDayDetailModel,
    modifier: Modifier = Modifier
) {
    val closingVsAvgColor = getComparisonColor(detail.ClosingPrice, avg?.MonthlyAveragePrice)
    val openingVsAvgColor = getComparisonColor(detail.OpeningPrice, avg?.MonthlyAveragePrice)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // --- 第一行：主標題 (1, 2, 4, 7) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                // 1. 股票代號：從 labelSmall 提升，並明確指定大小
                Text(
                    text = detail.Code,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp,           // 加大到 16.sp
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                // 2. 股票名稱：同步確保名稱夠大
                Text(
                    text = detail.Name,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 24.sp,           // 名稱可以給到 24.sp，非常醒目
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                // 4. 收盤價
                Text(
                    text = "$ ${detail.ClosingPrice}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
//                    color = getPriceColor(detail.Change)
                    color = closingVsAvgColor
                )
                // 7. 漲跌價差
                Text(
                    text = detail.Change,
                    style = MaterialTheme.typography.bodyMedium,
                    color = getPriceColor(detail.Change)
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

        // --- 第二行：價格細節 (3, 5, 6, 8) ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            SmallInfoColumn("開盤", detail.OpeningPrice, valueColor = openingVsAvgColor) // 3
            SmallInfoColumn("最高", detail.HighestPrice) // 5
            SmallInfoColumn("最低", detail.LowestPrice) // 6
            SmallInfoColumn("月均價", avg?.MonthlyAveragePrice ?: "--", valueColor = Color.Gray) // 8
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- 第三行：成交資訊 (9, 10, 11) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SmallInfoColumn("成交筆數", detail.Transaction) // 9
            SmallInfoColumn("成交股數", detail.TradeVolume) // 10
            SmallInfoColumn("成交金額", detail.TradeValue) // 11
        }
    }
}

@Composable
fun getComparisonColor(priceStr: String, avgStr: String?): Color {
    val price = priceStr.toDoubleOrNull() ?: 0.0
    val avg = avgStr?.toDoubleOrNull() ?: 0.0

    return when {
        avg == 0.0 -> MaterialTheme.colorScheme.onSurface // 若無均價資料，顯示原色
        price > avg -> Color(0xFFFF4545) // 高於均價：紅字
        price < avg -> Color(0xFF00C853) // 低於均價：綠字
        else -> MaterialTheme.colorScheme.onSurface    // 等於均價：原色
    }
}

@Composable
fun SmallInfoColumn(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface // 預設為文字原色
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = valueColor // 套用動態顏色
        )
    }
}

@Composable
fun getPriceColor(change: String): Color {
    return when {
        change.startsWith("+") -> Color(0xFFFF4545) // 台灣漲：紅
        change.startsWith("-") -> Color(0xFF00C853) // 台灣跌：綠
        else -> MaterialTheme.colorScheme.onSurface    // 平盤
    }
}