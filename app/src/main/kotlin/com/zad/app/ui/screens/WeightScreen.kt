package com.zad.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zad.app.R
import com.zad.app.ui.ZadViewModel
import com.zad.app.ui.components.LineChart
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(vm: ZadViewModel, onBack: () -> Unit) {
    val entries by vm.recentWeights.collectAsStateWithLifecycle()
    val latest by vm.latestWeight.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.weight_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            Surface(
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.weight_current), style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(latest?.kg?.let { "%.1f".format(it) } ?: "—",
                            style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.width(8.dp))
                        Text("كغ", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it.filter { c -> c.isDigit() || c == '.' }.take(5) },
                    label = { Text(stringResource(R.string.weight_log)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = {
                        val kg = input.toDoubleOrNull()
                        if (kg != null && kg in 30.0..300.0) {
                            vm.logBodyWeight(kg)
                            input = ""
                        }
                    },
                    enabled = (input.toDoubleOrNull() ?: 0.0) in 30.0..300.0
                ) { Text(stringResource(R.string.weight_save)) }
            }

            Spacer(Modifier.height(28.dp))
            Text(stringResource(R.string.weight_chart), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().height(200.dp)
            ) {
                if (entries.size < 2) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.stats_no_data),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    val points = entries
                        .sortedBy { it.recordedAtMs }
                        .map { it.recordedAtMs to it.kg }
                    LineChart(points = points, modifier = Modifier.padding(12.dp))
                }
            }

            Spacer(Modifier.height(20.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(entries, key = { it.id }) { e ->
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("%.1f كغ".format(e.kg),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f))
                            Text(formatDate(e.recordedAtMs),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(ms: Long): String {
    val f = SimpleDateFormat("EEEE d MMMM", Locale("ar"))
    return f.format(Date(ms))
}
