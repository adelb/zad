package com.zad.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zad.app.R
import com.zad.app.ml.Dish
import com.zad.app.ml.DishCatalog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishPickerSheet(onDismiss: () -> Unit, onPick: (Dish) -> Unit) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) DishCatalog.ALL
        else DishCatalog.ALL.filter { it.nameAr.contains(query.trim()) || it.id.contains(query.trim(), ignoreCase = true) }
    }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text(
                stringResource(R.string.picker_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.picker_search)) },
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn(Modifier.fillMaxWidth().heightIn(max = 480.dp)) {
                items(filtered, key = { it.id }) { dish ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPick(dish) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(dish.nameAr, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.weight(1f))
                        Text(
                            "${dish.kcalPer100g} ${stringResource(R.string.result_kcal)} / 100${stringResource(R.string.result_grams)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Divider()
                }
            }
        }
    }
}
