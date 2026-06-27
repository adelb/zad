package com.zad.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.zad.app.data.ActivityLevel
import com.zad.app.data.Goal
import com.zad.app.data.Profile
import com.zad.app.data.Sex
import com.zad.app.ui.ZadViewModel
import com.zad.app.ui.components.DobField
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: ZadViewModel, onBack: () -> Unit) {
    val p by vm.profile.collectAsStateWithLifecycle()

    var dob      by remember(p) { mutableStateOf(p?.birthDate ?: LocalDate.now().minusYears(30)) }
    var height   by remember(p) { mutableStateOf(p?.heightCm?.toString() ?: "170") }
    var weight   by remember(p) { mutableStateOf(p?.weightKg?.toString() ?: "75") }
    var sex      by remember(p) { mutableStateOf(p?.sex ?: Sex.MALE) }
    var activity by remember(p) { mutableStateOf(p?.activity ?: ActivityLevel.MODERATE) }
    var goal     by remember(p) { mutableStateOf(p?.goal ?: Goal.MAINTAIN) }

    val hNum = height.toIntOrNull()
    val wNum = weight.toDoubleOrNull()
    val valid = (hNum != null && hNum in 120..230) &&
                (wNum != null && wNum in 30.0..250.0) &&
                dob.isBefore(LocalDate.now().minusYears(10))
    val preview: Profile? = if (valid) Profile(dob, sex, hNum!!, wNum!!, activity, goal) else null

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
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
                .padding(horizontal = 22.dp, vertical = 16.dp)
        ) {
            DobField(value = dob, onChange = { dob = it })
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = height, onValueChange = { height = it.filter(Char::isDigit).take(3) },
                    label = { Text(stringResource(R.string.onb_height)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, modifier = Modifier.weight(1f))
                OutlinedTextField(value = weight, onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' }.take(5) },
                    label = { Text(stringResource(R.string.onb_weight)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(20.dp))

            SectionLabel(stringResource(R.string.onb_sex))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Sex.values().forEach { s ->
                    Chip(label = stringResource(s.labelRes), selected = sex == s,
                        onClick = { sex = s }, modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(20.dp))
            SectionLabel(stringResource(R.string.onb_activity))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ActivityLevel.values().forEach { a ->
                    Row1(
                        title = stringResource(a.labelRes),
                        desc  = stringResource(a.descRes),
                        selected = activity == a,
                        onClick = { activity = a }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            SectionLabel(stringResource(R.string.onb_goal))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Goal.values().forEach { g ->
                    Chip(label = stringResource(g.labelRes), selected = goal == g,
                        onClick = { goal = g }, modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(24.dp))
            preview?.let { pv ->
                Surface(color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(stringResource(R.string.budget_target), style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("${pv.dailyTargetKcal}", style = MaterialTheme.typography.displayLarge)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.result_kcal), style = MaterialTheme.typography.titleLarge)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("${stringResource(R.string.bmi_label)}: ${"%.1f".format(pv.bmi)} · ${stringResource(pv.bmiCategory.labelRes)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            Button(
                onClick = { preview?.let { vm.saveProfile(it); onBack() } },
                enabled = valid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.profile_save))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun Chip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(color = bg, contentColor = fg, shape = RoundedCornerShape(14.dp),
        modifier = modifier.clickable(onClick = onClick)) {
        Box(Modifier.padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun Row1(title: String, desc: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(desc, style = MaterialTheme.typography.bodySmall,
                color = (if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                    .copy(alpha = 0.85f))
        }
    }
}
