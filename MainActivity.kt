package com.example.week2_assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DevClusterScreen()
                }
            }
        }
    }
}

// Stateful Kapsayıcı
@Composable
fun DevClusterScreen() {
    // 1. Konfigürasyon Değişim Koruması: Ekran döndüğünde arama metni kaybolmaz[cite: 1]
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // 2. Çalışma Zamanı Hafıza Kilidi (Slot Table) & Düzeltilmiş Çarpım İşlemi[cite: 1]
    val totalCost = remember(sampleHardwareList) {
        sampleHardwareList.sumOf { it.cost * it.stockCount }
    }

    // 3. Turnike Mantığı (Memoization): Gereksiz CPU tüketimini önler[cite: 1]
    val filteredList = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            sampleHardwareList
        } else {
            sampleHardwareList.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "DevCluster Kaynak Yönetim Paneli",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Toplam Ekipman Değeri: $$totalCost",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Stateless Arama Çubuğu
        HardwareSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. LazyColumn Mimarisi ve key kullanımı (Nokta Atışı Recomposition)[cite: 2]
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = filteredList,
                key = { item -> item.id } // Benzersiz kimlik tanımlaması zorunlu[cite: 2]
            ) { item ->
                HardwareCard(
                    item = item,
                    onItemClick = { /* Tıklama olayı */ }
                )
            }
        }
    }
}

// Saf Fonksiyon Tasarımı (Stateless UI)[cite: 2]
@Composable
fun HardwareSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Donanım veya Kategori Ara...") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

// Saf Fonksiyon Tasarımı (Stateless UI)[cite: 2]
@Composable
fun HardwareCard(
    item: HardwareItem,
    onItemClick: (HardwareItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // Modifier Sıralaması: Önce .clickable(), ardından .padding()[cite: 2]
            .clickable { onItemClick(item) }
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Kategori: ${item.category}", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Fiyat: $${item.cost}")
                Text(
                    text = if (item.stockCount > 0) "Stok: ${item.stockCount}" else "Stok Tükenmiş",
                    color = if (item.stockCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
