package com.hao.cubc.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hao.cubc.data.model.StockAvgPriceModel
import com.hao.cubc.data.model.StockDayDetailModel
import com.hao.cubc.data.model.StockPeModel
import com.hao.cubc.ui.CategoryManager
import com.hao.cubc.ui.FavoriteManager

enum class StockFilterOption {
    CODE_DESC,  // 依股票代號降序
    CODE_ASC,   // 依股票代號升序
    ETF_ONLY    // ETF
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockMainScreen(
    stockData: Triple<List<StockPeModel>, List<StockAvgPriceModel>, List<StockDayDetailModel>>,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    categoryManager: CategoryManager,
    favoriteManager: FavoriteManager
){
    val (peList, avgList, detailList) = stockData

    // 統一狀態定義在最上方
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var searchQuery by remember { mutableStateOf("") }
    var currentOption by remember { mutableStateOf(StockFilterOption.CODE_ASC) }
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    var favoriteList by remember { mutableStateOf<Set<String>>(emptySet()) }
    var inventoryList by remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(Unit) {
        favoriteList = favoriteManager.getFavorites()
        inventoryList = favoriteManager.getInventory()
    }
    // 取得顯示list, 支援sort, search, import(json), 最愛, 庫存 
    val displayList = remember(
        detailList,
        searchQuery,
        currentOption,
        selectedCategoryName,
        favoriteList,
        inventoryList
    ) {
        if (searchQuery.isNotEmpty()) {
            // 優先權 1：搜尋
            detailList.filter {
                it.Code.contains(searchQuery, ignoreCase = true) ||
                        it.Name.contains(searchQuery, ignoreCase = true)
            }
        } else if (!selectedCategoryName.isNullOrEmpty()) {
            // 優先權 2：分類過濾 (包含 JSON 分類 與 使用者自定義分類)
            when (selectedCategoryName) {
                "我的最愛" -> detailList.filter { it.Code in favoriteList }
                "個人庫存" -> detailList.filter { it.Code in inventoryList }
                else -> {
                    // 原有的動態 JSON 分類邏輯
                    val targetCodes = categoryManager.dynamicCategories[selectedCategoryName] ?: emptySet()
                    detailList.filter { it.Code in targetCodes }
                }
            }
        } else {
            // 優先權 3：基本排序
            when (currentOption) {
                StockFilterOption.CODE_DESC -> detailList.sortedByDescending { it.Code }
                StockFilterOption.ETF_ONLY -> detailList.filter { it.Code.startsWith("00") || it.Code.startsWith("01") }
                else -> detailList.sortedBy { it.Code }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("台股即時快訊") },
                actions = {
                    IconButton(onClick = onThemeToggle) {
                        Icon(if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode, "主題")
                    }
                    IconButton(onClick = { showSheet = true }) {
                        Icon(Icons.Default.FilterList, "篩選")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 搜尋框
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("搜尋代碼或名稱") },
                singleLine = true
            )

            // 顯示清單
            StockListScreen(
                displayList = displayList,
                detailList = detailList,
                peList = peList,
                avgList = avgList,
                // 傳遞狀態
                favoriteList = favoriteList,
                inventoryList = inventoryList,
                // 傳遞「怎麼修改」的邏輯
                onFavoriteToggle = { code ->
                    val newSet = favoriteList.toMutableSet()
                    if (code in newSet) newSet.remove(code) else newSet.add(code)
                    favoriteList = newSet
                    favoriteManager.saveFavorites(newSet) // 這裡才處理 Manager 儲存
                },
                onInventoryToggle = { code ->
                    val newSet = inventoryList.toMutableSet()
                    if (code in newSet) newSet.remove(code) else newSet.add(code)
                    inventoryList = newSet
                    favoriteManager.saveInventory(newSet)
                }
            )
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            FilterMenuContent(
                categoryManager = categoryManager, // 傳入你初始化的 Manager
                onSortSelected = { option ->
                    currentOption = option
                    selectedCategoryName = null // 清除分類過濾
                    showSheet = false
                },
                onCategorySelected = { categoryName ->
                    selectedCategoryName = categoryName // 設定 JSON 分類
                    showSheet = false
                }
            )
        }
    }
}

@Composable
fun FilterMenuContent(
    categoryManager: CategoryManager,
    onSortSelected: (StockFilterOption) -> Unit,
    onCategorySelected: (String) -> Unit
) {
    val dynamicKeys = categoryManager.dynamicCategories.keys.toList()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // --- 標題：清除功能 ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("篩選與分類", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                TextButton(onClick = {
                    onSortSelected(StockFilterOption.CODE_ASC)
                    onCategorySelected("")
                }) {
                    Icon(Icons.Default.ClearAll, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("清除重設")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- 區塊 1：我的設定 (個人化) ---
        item { SectionHeader("個人化追蹤") }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModernFilterChip(Modifier.weight(1f), "⭐ 我的最愛", MaterialTheme.colorScheme.primaryContainer) {
                    onCategorySelected("我的最愛")
                }
                ModernFilterChip(Modifier.weight(1f), "💼 個人庫存", MaterialTheme.colorScheme.secondaryContainer) {
                    onCategorySelected("個人庫存")
                }
            }
        }

        // --- 區塊 2：基本排序 ---
        item { Spacer(Modifier.height(24.dp)) }
        item { SectionHeader("基本排序") }
        val sortOptions = listOf(
            "依股票代號降序" to StockFilterOption.CODE_DESC,
            "依股票代號升序" to StockFilterOption.CODE_ASC,
            "ETF" to StockFilterOption.ETF_ONLY
        )
        items(sortOptions) { (label, option) ->
            SimpleMenuRow(label) { onSortSelected(option) }
        }

        // --- 區塊 3：產業分類 ---
        item { Spacer(Modifier.height(24.dp)) }
        item { SectionHeader("產業主題分類") }
        items(dynamicKeys) { categoryName ->
            SimpleMenuRow("# $categoryName") { onCategorySelected(categoryName) }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

// 輔助組件：區塊標題
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

// 輔助組件：現代感小卡片
@Composable
fun ModernFilterChip(modifier: Modifier, label: String, containerColor: Color, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = containerColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 12.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

// 輔助組件：簡約行樣式
@Composable
fun SimpleMenuRow(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = Color.Transparent
    ) {
        Column {
            Text(
                text = label,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}
