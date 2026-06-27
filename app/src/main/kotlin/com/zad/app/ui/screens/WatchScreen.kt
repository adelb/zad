package com.zad.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zad.app.health.HcStatus
import com.zad.app.health.HealthIntents
import com.zad.app.ui.ZadViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchScreen(vm: ZadViewModel, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val hc by vm.healthReading.collectAsStateWithLifecycle()
    var manualK by remember { mutableStateOf("") }
    var manualSteps by remember { mutableStateOf("") }
    val hcLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        vm.healthBridge.permissionContract()
    ) { vm.refreshHealthConnect() }

    LaunchedEffect(Unit) { vm.refreshHealthConnect() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الساعة والصحة") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { vm.refreshHealthConnect() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "تحديث")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Diagnostic card ──
            DiagnosticCard(
                hcStatus = hc.status,
                granted = hc.granted,
                sources = hc.sources,
                lastUpdatedMs = hc.lastUpdatedMs,
                error = hc.error,
                onConnect = { hcLauncher.launch(vm.healthBridge.permissions) },
                onOpenSettings = { HealthIntents.openHealthConnectSettings(ctx) },
                onInstall = { HealthIntents.installHealthConnect(ctx) }
            )

            // ── Today's HC numbers ──
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("قراءة اليوم", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        BigStat("سعرات نشطة", "${hc.activeKcal}", Modifier.weight(1f))
                        BigStat("خطوات", "${hc.steps}", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        BigStat("مسافة",
                            if (hc.distanceMeters > 999) "${"%.1f".format(hc.distanceMeters/1000.0)} كم"
                            else "${hc.distanceMeters} م", Modifier.weight(1f))
                        BigStat("دقائق نشاط", "${hc.exerciseMinutes} د", Modifier.weight(1f))
                    }
                }
            }

            // ── Manual override ──
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("إدخال يدوي من ساعتك",
                        style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "إن كانت ساعتك (مثل Huawei) لا تكتب لـ Health Connect، أدخل القيم من تطبيق الساعة وستُحتسب ضمن سعرات اليوم.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = manualK,
                            onValueChange = { manualK = it.filter(Char::isDigit).take(4) },
                            label = { Text("سعرات نشطة") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = manualSteps,
                            onValueChange = { manualSteps = it.filter(Char::isDigit).take(6) },
                            label = { Text("خطوات") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                vm.setManualWatch(
                                    (manualK.toIntOrNull() ?: 0),
                                    (manualSteps.toIntOrNull() ?: 0)
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("حفظ") }
                        OutlinedButton(
                            onClick = { vm.setManualWatch(0, 0); manualK = ""; manualSteps = "" },
                            modifier = Modifier.weight(1f)
                        ) { Text("مسح") }
                    }
                    if (hc.manualKcal > 0) {
                        Spacer(Modifier.height(8.dp))
                        Text("القيمة اليدوية الحالية: ${hc.manualKcal} سعرة",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            // ── Huawei note ──
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("هل تستخدم ساعة Huawei؟",
                        style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "تطبيق Huawei Health لا يكتب لـ Health Connect تلقائيًا على معظم الأجهزة. "
                        + "الحلول: (١) استخدم إدخالًا يدويًا أعلاه — افتح Huawei Health، خذ رقم السعرات النشطة، والصق هنا. "
                        + "(٢) ادعم إصدار Huawei Health Kit المخصص (يتطلب حساب مطوّر منفصل + توقيع — لاحقًا).",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { HealthIntents.openHuaweiHealth(ctx) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("افتح Huawei Health / ثبّته") }
                }
            }
        }
    }
}

@Composable
private fun DiagnosticCard(
    hcStatus: HcStatus,
    granted: Boolean,
    sources: Set<String>,
    lastUpdatedMs: Long,
    error: String?,
    onConnect: () -> Unit,
    onOpenSettings: () -> Unit,
    onInstall: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp)) {
            Text("الحالة", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            DiagRow(
                label = "Health Connect",
                ok = hcStatus == HcStatus.AVAILABLE,
                value = when (hcStatus) {
                    HcStatus.AVAILABLE -> "مثبّت"
                    HcStatus.NEEDS_UPDATE -> "يحتاج تحديث"
                    HcStatus.NOT_INSTALLED -> "غير مثبّت"
                }
            )
            DiagRow(
                label = "الإذن",
                ok = granted,
                value = if (granted) "ممنوح" else "غير ممنوح"
            )
            DiagRow(
                label = "مصادر اليوم",
                ok = sources.isNotEmpty(),
                value = if (sources.isEmpty()) "لا يوجد"
                else sources.joinToString(" · ") { friendlyName(it) }
            )
            if (lastUpdatedMs > 0) {
                Spacer(Modifier.height(4.dp))
                Text("آخر تحديث: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(lastUpdatedMs))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (!error.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text("خطأ: $error", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(12.dp))

            when {
                hcStatus == HcStatus.NOT_INSTALLED -> {
                    Button(onClick = onInstall, modifier = Modifier.fillMaxWidth()) {
                        Text("ثبّت Health Connect")
                    }
                }
                hcStatus == HcStatus.NEEDS_UPDATE -> {
                    Button(onClick = onInstall, modifier = Modifier.fillMaxWidth()) {
                        Text("حدّث Health Connect")
                    }
                }
                !granted -> {
                    Button(onClick = onConnect, modifier = Modifier.fillMaxWidth()) {
                        Text("اربط الأذونات")
                    }
                }
                sources.isEmpty() -> {
                    Column {
                        Text(
                            "مصدر السعرات (الساعة / تطبيق الصحة) لا يكتب لـ Health Connect بعد. "
                            + "افتح إعدادات HC وتحقّق من المصادر، أو استخدم الإدخال اليدوي.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
                            Text("افتح إعدادات Health Connect")
                        }
                    }
                }
                else -> {
                    OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
                        Text("افتح إعدادات Health Connect")
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagRow(label: String, ok: Boolean, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(
            if (ok) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = null,
            tint = if (ok) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun BigStat(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

private fun friendlyName(pkg: String): String = when (pkg) {
    "com.samsung.android.shealth" -> "Samsung Health"
    "com.fitbit.android" -> "Fitbit"
    "com.google.android.apps.fitness" -> "Google Fit"
    "com.xiaomi.hm.health" -> "Mi Fitness"
    "com.xiaomi.wearable" -> "Mi Wearable"
    "com.huawei.health" -> "Huawei Health"
    "com.garmin.android.apps.connectmobile" -> "Garmin Connect"
    else -> pkg
}
