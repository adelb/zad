package com.zad.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zad.app.R
import com.zad.app.ui.ZadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterScreen(vm: ZadViewModel, onBack: () -> Unit) {
    val ml by vm.todayWaterMl.collectAsStateWithLifecycle()
    val profile by vm.profile.collectAsStateWithLifecycle()
    val target = profile?.dailyWaterMl ?: 2500
    val ratio = (ml.toFloat() / target).coerceIn(0f, 1f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.water_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { vm.resetTodayWater() }) {
                        Icon(Icons.Default.Refresh, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.water_today), style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("$ml", style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.width(8.dp))
                        Text("/ $target مل", style = MaterialTheme.typography.titleLarge)
                    }
                    Spacer(Modifier.height(14.dp))
                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
                    )
                    Spacer(Modifier.height(8.dp))
                    if (ml >= target) {
                        Text(stringResource(R.string.water_done),
                            style = MaterialTheme.typography.titleMedium)
                    } else {
                        Text("${stringResource(R.string.water_remaining)}: ${target - ml} مل",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // glasses grid — 250 ml each
            val glasses = (target / 250).coerceAtLeast(8)
            val filled = (ml / 250).coerceAtMost(glasses)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp)
            ) {
                val rows = (glasses + 7) / 8
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    for (r in 0 until rows) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            for (c in 0 until 8) {
                                val idx = r * 8 + c
                                if (idx >= glasses) Spacer(Modifier.size(34.dp))
                                else {
                                    val on = idx < filled
                                    Surface(
                                        color = if (on) MaterialTheme.colorScheme.secondary
                                                else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.size(34.dp)
                                    ) {}
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { vm.addWater(250) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(stringResource(R.string.water_add_glass),
                    style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
