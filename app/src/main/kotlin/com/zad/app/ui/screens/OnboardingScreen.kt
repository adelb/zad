package com.zad.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zad.app.R
import com.zad.app.data.ActivityLevel
import com.zad.app.data.Goal
import com.zad.app.data.Profile
import com.zad.app.data.Sex

@Composable
fun OnboardingScreen(onFinish: (Profile) -> Unit) {
    var age by remember { mutableStateOf("30") }
    var height by remember { mutableStateOf("170") }
    var weight by remember { mutableStateOf("75") }
    var sex by remember { mutableStateOf(Sex.MALE) }
    var activity by remember { mutableStateOf(ActivityLevel.MODERATE) }
    var goal by remember { mutableStateOf(Goal.MAINTAIN) }

    val ageNum = age.toIntOrNull()
    val hNum   = height.toIntOrNull()
    val wNum   = weight.toDoubleOrNull()
    val valid  = (ageNum != null && ageNum in 10..100) &&
                 (hNum != null && hNum in 120..230) &&
                 (wNum != null && wNum in 30.0..250.0)

    val preview: Profile? = if (valid) Profile(ageNum!!, sex, hNum!!, wNum!!, activity, goal) else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 28.dp)
    ) {
        Text(stringResource(R.string.onb_title), style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(6.dp))
        Text(stringResource(R.string.onb_sub),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter(Char::isDigit).take(3) },
                label = { Text(stringResource(R.string.onb_age)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = height,
                onValueChange = { height = it.filter(Char::isDigit).take(3) },
                label = { Text(stringResource(R.string.onb_height)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' }.take(5) },
                label = { Text(stringResource(R.string.onb_weight)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(20.dp))

        SectionLabel(stringResource(R.string.onb_sex))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Sex.values().forEach { s ->
                ChoiceChip(
                    label = stringResource(s.labelRes),
                    selected = sex == s,
                    onClick = { sex = s },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        SectionLabel(stringResource(R.string.onb_activity))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            ActivityLevel.values().forEach { a ->
                ChoiceRow(
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
                ChoiceChip(
                    label = stringResource(g.labelRes),
                    selected = goal == g,
                    onClick = { goal = g },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Live preview of the calculation
        preview?.let { p ->
            Surface(
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.budget_target),
                        style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("${p.dailyTargetKcal}",
                            style = MaterialTheme.typography.displayLarge)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.result_kcal),
                            style = MaterialTheme.typography.titleLarge)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "${stringResource(R.string.bmi_label)}: ${"%.1f".format(p.bmi)} · ${stringResource(p.bmiCategory.labelRes)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        Button(
            onClick = { preview?.let(onFinish) },
            enabled = valid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (preview == null) stringResource(R.string.onb_continue)
                 else stringResource(R.string.onb_finish))
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun ChoiceChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        color = bg, contentColor = fg, shape = RoundedCornerShape(14.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(Modifier.padding(vertical = 14.dp), contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun ChoiceRow(title: String, desc: String, selected: Boolean, onClick: () -> Unit) {
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
