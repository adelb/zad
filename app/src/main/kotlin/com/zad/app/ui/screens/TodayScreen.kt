package com.zad.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WarningAmber
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
import com.zad.app.data.Profile
import com.zad.app.ui.ZadViewModel

@Composable
fun TodayScreen(
    vm: ZadViewModel,
    onCapture: () -> Unit,
    onOpenProfile: () -> Unit = {},
    onOpenWater: () -> Unit = {},
    onOpenWeight: () -> Unit = {}
) {
    val total by vm.todayTotal.collectAsStateWithLifecycle()
    val entries by vm.todayEntries.collectAsStateWithLifecycle()
    val perMeal by vm.perMealToday.collectAsStateWithLifecycle()
    val profile by vm.profile.collectAsStateWithLifecycle()
    val waterMl by vm.todayWaterMl.collectAsStateWithLifecycle()
    val latestWeight by vm.latestWeight.collectAsStateWithLifecycle()
    val burnFromWorkouts by vm.caloriesBurnedToday.collectAsStateWithLifecycle()
    val hc by vm.healthReading.collectAsStateWithLifecycle()
    val hcLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        vm.healthBridge.permissionContract()
    ) { vm.refreshHealthConnect() }
    androidx.compose.runtime.LaunchedEffect(Unit) { vm.refreshHealthConnect() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
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
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onOpenProfile) {
                Icon(Icons.Default.Person, contentDescription = stringResource(R.string.profile_title))
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(stringResource(R.string.tagline),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))

        BudgetCard(profile = profile, consumed = total)

        Spacer(Modifier.height(12.dp))

        OverBudgetBanner(profile = profile, consumed = total, entries = entries)

        WeighInPrompt(latestMs = latestWeight?.recordedAtMs, onClick = onOpenWeight)

        Spacer(Modifier.height(16.dp))

        HealthBurnRow(
            workoutBurn = burnFromWorkouts,
            healthBurn = hc.activeKcal,
            steps = hc.steps,
            hcStatus = hc.status,
            granted = hc.granted,
            onConnect = {
                hcLauncher.launch(vm.healthBridge.permissions)
            }
        )

        Spacer(Modifier.height(16.dp))

        WaterRow(currentMl = waterMl, targetMl = profile?.dailyWaterMl ?: 2500, onOpen = onOpenWater)

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
private fun BudgetCard(profile: Profile?, consumed: Int) {
    val target = profile?.dailyTargetKcal
    val remaining = target?.let { (it - consumed).coerceAtLeast(0) }
    val over = if (target != null) (consumed - target).coerceAtLeast(0) else 0
    val pct = if (target != null && target > 0) (consumed.toFloat() / target).coerceIn(0f, 1.2f) else 0f

    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(22.dp)) {
            if (target == null) {
                Text(stringResource(R.string.today_total), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("$consumed", style = MaterialTheme.typography.displayLarge)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.result_kcal), style = MaterialTheme.typography.titleLarge)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(R.string.budget_left),
                            style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("${remaining ?: 0}", style = MaterialTheme.typography.displayLarge)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.result_kcal),
                                style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { pct },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f),
                )
                Spacer(Modifier.height(10.dp))
                Row {
                    Text(
                        "${stringResource(R.string.budget_eaten)}: $consumed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        "${stringResource(R.string.budget_target)}: $target",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }
                if (over > 0) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "تجاوزت بـ $over ${stringResource(R.string.result_kcal)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun OverBudgetBanner(profile: Profile?, consumed: Int, entries: List<MealEntry>) {
    val target = profile?.dailyTargetKcal ?: return
    val ratio = consumed.toFloat() / target

    when {
        ratio > 1f -> {
            // suggest reducing the largest entry of the day
            val biggest = entries.maxByOrNull { it.calories }
            val suggestion = biggest?.let {
                "جرّب تقليل ${it.dishNameAr} (${it.calories} سعرة)"
            } ?: stringResource(R.string.budget_over_hint)

            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WarningAmber, null)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(R.string.budget_over),
                            style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(2.dp))
                        Text(suggestion, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        ratio >= 0.9f -> {
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WarningAmber, null)
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(R.string.budget_near),
                        style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        else -> { /* no banner */ }
    }
}

@Composable
private fun HealthBurnRow(
    workoutBurn: Int,
    healthBurn: Int,
    steps: Int,
    hcStatus: com.zad.app.health.HcStatus,
    granted: Boolean,
    onConnect: () -> Unit
) {
    val totalBurn = workoutBurn + healthBurn
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("سعرات محروقة اليوم",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f))
                Text(
                    "$totalBurn سعرة",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("التمارين: $workoutBurn",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("الساعة/الصحة: $healthBurn",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (steps > 0) {
                    Text("خطوات: $steps",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (!granted) {
                Spacer(Modifier.height(8.dp))
                when (hcStatus) {
                    com.zad.app.health.HcStatus.AVAILABLE -> {
                        OutlinedButton(onClick = onConnect, modifier = Modifier.fillMaxWidth()) {
                            Text("اربط بـ Health Connect لقراءة السعرات من ساعتك")
                        }
                    }
                    com.zad.app.health.HcStatus.NEEDS_UPDATE -> {
                        Text(
                            "حدّث تطبيق Health Connect من متجر Play لقراءة بيانات الساعة",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    com.zad.app.health.HcStatus.NOT_INSTALLED -> {
                        Text(
                            "Health Connect غير مثبّت — ثبّته من متجر Play لقراءة سعرات الساعة. " +
                            "أجهزة Huawei قد لا تكتب لـ Health Connect مباشرة.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WaterRow(currentMl: Int, targetMl: Int, onOpen: () -> Unit) {
    val ratio = (currentMl.toFloat() / targetMl).coerceIn(0f, 1f)
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocalDrink, null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.water_today), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { ratio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text("$currentMl / $targetMl",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun WeighInPrompt(latestMs: Long?, onClick: () -> Unit) {
    val staleAfterMs = 7L * 24 * 60 * 60 * 1000
    val isStale = latestMs == null || (System.currentTimeMillis() - latestMs) >= staleAfterMs
    if (!isStale) return
    Spacer(Modifier.height(12.dp))
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MonitorWeight, null)
            Spacer(Modifier.width(10.dp))
            Text(stringResource(R.string.weight_weekly_prompt),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f))
            Text("→", style = MaterialTheme.typography.titleLarge)
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
