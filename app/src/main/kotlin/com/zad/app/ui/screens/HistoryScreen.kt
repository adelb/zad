package com.zad.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.zad.app.R
import com.zad.app.data.MealEntry
import com.zad.app.ui.ZadViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HistoryScreen(vm: ZadViewModel) {
    val entries by vm.history.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(stringResource(R.string.history_title), style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(16.dp))
        if (entries.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.history_empty),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center)
            }
        } else {
            val grouped = remember(entries) { entries.groupBy { it.dayKey } }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                grouped.forEach { (day, dayEntries) ->
                    item(key = day) {
                        Text(formatDay(day),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp))
                    }
                    items(dayEntries, key = { it.id }) { e -> HistoryRow(e) }
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(entry: MealEntry) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (entry.photoPath != null) {
                AsyncImage(
                    model = entry.photoPath,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(Modifier.width(12.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(entry.dishNameAr, style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(entry.mealType.labelRes) + " · ${entry.grams} " +
                            stringResource(R.string.result_grams),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text("${entry.calories}", style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}

private fun formatDay(dayKey: String): String = runCatching {
    val parsed = SimpleDateFormat("yyyy-MM-dd", Locale("ar")).parse(dayKey) ?: return dayKey
    SimpleDateFormat("EEEE d MMMM", Locale("ar")).format(parsed)
}.getOrDefault(dayKey)
