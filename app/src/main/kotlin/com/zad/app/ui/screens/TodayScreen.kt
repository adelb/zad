package com.zad.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
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
import com.zad.app.data.MealType
import com.zad.app.ui.ZadViewModel

@Composable
fun TodayScreen(vm: ZadViewModel, onCapture: () -> Unit) {
    val total by vm.todayTotal.collectAsStateWithLifecycle()
    val entries by vm.todayEntries.collectAsStateWithLifecycle()
    val perMeal by vm.perMealToday.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(R.drawable.ic_zad_mark),
                contentDescription = null,
                modifier = Modifier.size(width = 28.dp, height = 36.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.displaySmall)
        }
        Spacer(Modifier.height(4.dp))
        Text(stringResource(R.string.tagline),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))

        TotalCard(total = total)

        Spacer(Modifier.height(16.dp))

        Text(stringResource(R.string.today_per_meal), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        PerMealRow(perMeal)

        Spacer(Modifier.height(20.dp))

        if (entries.isEmpty()) {
            EmptyState(onCapture)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(entries, key = { it.id }) { entry ->
                    EntryRow(entry, onDelete = { vm.deleteEntry(entry) })
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onCapture,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PhotoCamera, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.tab_capture))
            }
        }
    }
}

@Composable
private fun TotalCard(total: Int) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(24.dp)) {
            Text(stringResource(R.string.today_total), style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$total", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.result_kcal), style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun PerMealRow(rows: List<com.zad.app.data.MealTotal>) {
    val map = rows.associate { it.mealType to it.totalCalories }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        MealType.values().forEach { t ->
            MealChip(label = stringResource(t.labelRes), kcal = map[t] ?: 0, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MealChip(label: String, kcal: Int, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text("$kcal", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun EntryRow(entry: MealEntry, onDelete: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (entry.photoPath != null) {
                AsyncImage(
                    model = entry.photoPath,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(entry.dishNameAr, style = MaterialTheme.typography.titleMedium)
                Text(
                    stringResource(entry.mealType.labelRes) + " · ${entry.grams} " +
                            stringResource(R.string.result_grams),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "${entry.calories}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.result_kcal), style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = onDelete) {
                Text(stringResource(R.string.delete),
                    color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun EmptyState(onCapture: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.today_empty),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(stringResource(R.string.today_empty_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onCapture) {
            Icon(Icons.Default.PhotoCamera, null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.tab_capture))
        }
    }
}
