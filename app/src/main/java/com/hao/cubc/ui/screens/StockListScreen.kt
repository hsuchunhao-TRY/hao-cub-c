
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

@Composable
fun StockListScreen(
    detailList: List<StockDayDetailModel>, // 主清單 (例如 1000 筆)
    peList: List<StockPeModel>,           // 基本面 (可能只有 500 筆)
    avgList: List<StockAvgPriceModel>      // 均價 (可能只有 800 筆)
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 以 detailList 為基準，因為它是你的 11 欄位核心
        items(detailList) { detailItem ->

            // --- 關鍵動作：配對 ---
            // 根據股票代號 (Code) 去另外兩個 List 找資料
            val peItem = peList.find { it.Code == detailItem.Code }
            val avgItem = avgList.find { it.Code == detailItem.Code }

            // --- 呼叫你的組件 ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                StockFrontContent(
                    detail = detailItem,
                    pe = peItem,   // 如果沒找到，find 會回傳 null，剛好符合你的參數定義
                    avg = avgItem
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