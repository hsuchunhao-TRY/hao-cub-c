package com.hao.cubc.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hao.cubc.data.model.StockDayDetailModel
import com.hao.cubc.data.model.StockPeModel

@Composable
fun StockBackContent(detail: StockDayDetailModel, pe: StockPeModel?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${detail.Name} (${detail.Code}) - 詳細指標",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            SmallInfoColumn("本益比", pe?.PEratio ?: "--")
            SmallInfoColumn("殖利率", pe?.DividendYield ?: "--")
            SmallInfoColumn("股價淨值比", pe?.PBratio ?: "--")
        }

        Spacer(modifier = Modifier.height(16.dp))

//        Text(
//            text = "最後更新時間：${java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))}",
//            style = MaterialTheme.typography.labelSmall,
//            color = Color.Gray
//        )
    }
}