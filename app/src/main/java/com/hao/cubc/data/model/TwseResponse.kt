package com.hao.cubc.data.model // 必須完全對應資料夾路徑

// 1. 本益比、殖利率模型
data class StockPeModel(
    val Date: String,
    val Code: String,
    val Name: String,
    val PEratio: String,
    val DividendYield: String,
    val PBratio: String
)

// 2. 均價 (STOCK_DAY_AVG_ALL)
data class StockAvgPriceModel(
    val Date: String,
    val Code: String,
    val Name: String,
    val ClosingPrice: String,
    val MonthlyAveragePrice: String
)

// 3. 日成交資訊模型 (個股詳情用)
data class StockDayDetailModel(
    val Date: String,
    val Code: String,
    val Name: String,
    val TradeVolume: String,
    val TradeValue: String,
    val OpeningPrice: String,
    val HighestPrice: String,
    val LowestPrice: String,
    val ClosingPrice: String,
    val Change: String,
    val Transaction: String
)
