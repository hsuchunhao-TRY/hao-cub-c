import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hao.cubc.ui.screens.StockMainScreen
import com.hao.cubc.viewmodel.StockViewModel

@Composable
fun MainApp(
    viewModel: StockViewModel,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit
) {
    // 💡 加入這段：確保進來時啟動輪詢
    LaunchedEffect(Unit) {
        viewModel.startPolling()
    }

    val data = viewModel.stockData

    if (data == null) {
        LoadingScreen()
    } else {
        StockMainScreen(
            stockData = data,
            isDarkMode = isDarkMode,
            onThemeToggle = onThemeToggle
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "正在獲取台股即時數據...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}