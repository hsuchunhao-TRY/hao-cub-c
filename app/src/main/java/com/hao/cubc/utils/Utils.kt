package com.hao.cubc.utils

import com.hao.cubc.data.model.StockDayDetailModel
import kotlinx.serialization.Serializable

@Serializable
data class StockGroupItem(
    val code: String,
    val name: String
)

@Serializable
data class StockGroupConfig(
    val aiList: List<StockGroupItem> = emptyList(),
    val shippingList: List<StockGroupItem> = emptyList(),
    val inventoryList: List<StockGroupItem> = emptyList(),
    val robotList: List<StockGroupItem> = emptyList(),
    val financeList: List<StockGroupItem> = emptyList(),
    val traditionalList: List<StockGroupItem> = emptyList()
)
data class StockSimpleModel(
    val code: String,
    val name: String
)

/**
 * 根據股票代碼進行排序
 * @param list 原始清單
 * @param lowToHigh true: 由小到大 (0050 -> 2330), false: 由大到小 (2330 -> 0050)
 */
fun sortByCode(
    list: List<StockDayDetailModel>,
    lowToHigh: Boolean
): List<StockDayDetailModel> {
    return if (lowToHigh) {
        list.sortedBy { it.Code }
    } else {
        list.sortedByDescending { it.Code }
    }
}

/**
 * 從總清單中過濾出 ETF 項目
 */
fun generateEtfList(allData: List<StockDayDetailModel>): List<StockDayDetailModel> {
    return allData.filter { item ->
        // 判斷條件：代碼以 "00" 或 "01" 開頭
        item.Code.startsWith("00") || item.Code.startsWith("01")
    }
}

/**
 * 將原始詳細資料轉換為精簡的「代碼/名稱」清單
 */
fun getSimpleStockList(fullList: List<StockDayDetailModel>): List<StockSimpleModel> {
    return fullList.map {
        StockSimpleModel(code = it.Code, name = it.Name)
    }.distinctBy { it.code } // 確保代碼不重複
}

/**
 * 根據關鍵字搜尋相似股票
 * @param fullList 原始的完整股票清單
 * @param query 使用者輸入的字串
 * @return 符合條件的過濾清單
 */
fun searchStocks(
    fullList: List<StockDayDetailModel>,
    query: String
): List<StockDayDetailModel> {
    // 如果搜尋字串是空的，直接回傳原清單
    if (query.isBlank()) return fullList

    val trimmedQuery = query.trim() // 去除前後空白

    return fullList.filter { stock ->
        // 同時比對「代碼」與「名稱」，只要其中一個包含關鍵字就成立
        // ignoreCase = true 確保搜尋 "2330" 或英文代碼時不分大小寫
        stock.Code.contains(trimmedQuery, ignoreCase = true) ||
        stock.Name.contains(trimmedQuery, ignoreCase = true)
    }
}

