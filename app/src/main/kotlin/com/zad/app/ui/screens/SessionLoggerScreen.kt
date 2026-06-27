package com.zad.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import com.zad.app.data.RoutineExercise
import com.zad.app.ui.ZadViewModel

/**
 * The "table" the user asked for — one row per exercise in the routine,
 * a sub-table of sets with weight + reps. Adding a set captures the date
 * automatically (recordedAtMs in the entity).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionLoggerScreen(
    vm: ZadViewModel,
    sessionId: Long,
    routineId: Long,
    onFinish: () -> Unit
) {
    val exercises by vm.exercisesForRoutine(routineId)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val sets by vm.setsForSession(sessionId)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val sessionKcal by vm.caloriesForSession(sessionId)
        .collectAsStateWithLifecycle(initialValue = 0)
    var pickerOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.workout_title)) },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            "$sessionKcal سعرة",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 1.dp) {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { pickerOpen = true }) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("تمرين")
                    }
                    Spacer(Modifier.weight(1f))
                    Button(onClick = onFinish, modifier = Modifier.weight(2f)) {
                        Text(stringResource(R.string.workout_finish))
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(exercises, key = { it.id }) { ex ->
                val setsForEx = sets.filter { it.exerciseId == ex.exerciseId }
                ExerciseBlock(
                    exercise = ex,
                    setsCount = setsForEx.size,
                    sets = setsForEx,
                    onAddSet = { weight, reps ->
                        vm.logSet(
                            sessionId = sessionId,
                            exerciseId = ex.exerciseId,
                            nameAr = ex.nameAr,
                            setNumber = setsForEx.size + 1,
                            weightKg = weight,
                            reps = reps
                        )
                    },
                    onDeleteSet = { id -> vm.deleteSet(id) }
                )
            }
        }
    }

    if (pickerOpen) {
        ExercisePickerSheet(
            onDismiss = { pickerOpen = false },
            onPick = { ex ->
                vm.appendExerciseToRoutine(routineId, ex.id)
                pickerOpen = false
            }
        )
    }
}

@Composable
private fun ExerciseBlock(
    exercise: RoutineExercise,
    setsCount: Int,
    sets: List<com.zad.app.data.ExerciseSet>,
    onAddSet: (Double, Int) -> Unit,
    onDeleteSet: (Long) -> Unit
) {
    var weightInput by remember(exercise.id) { mutableStateOf("") }
    var repsInput   by remember(exercise.id) { mutableStateOf(exercise.targetReps.toString()) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(exercise.nameAr, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "هدف: ${exercise.targetSets} × ${exercise.targetReps}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "$setsCount/${exercise.targetSets}",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            if (sets.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                // table header
                Row {
                    HeaderCell(stringResource(R.string.workout_set), 0.7f)
                    HeaderCell(stringResource(R.string.workout_weight), 1.2f)
                    HeaderCell(stringResource(R.string.workout_reps), 1.0f)
                    HeaderCell("سعرة", 0.9f)
                    Spacer(Modifier.width(40.dp))
                }
                Divider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                sets.forEach { s ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        BodyCell("${s.setNumber}", 0.7f)
                        BodyCell("${s.weightKg}", 1.2f)
                        BodyCell("${s.reps}", 1.0f)
                        BodyCell("${s.caloriesEstimate}", 0.9f)
                        IconButton(onClick = { onDeleteSet(s.id) }, modifier = Modifier.size(36.dp)) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it.filter { c -> c.isDigit() || c == '.' }.take(6) },
                    label = { Text(stringResource(R.string.workout_weight)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = repsInput,
                    onValueChange = { repsInput = it.filter(Char::isDigit).take(3) },
                    label = { Text(stringResource(R.string.workout_reps)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            val canAdd = (weightInput.toDoubleOrNull() ?: -1.0) >= 0 && (repsInput.toIntOrNull() ?: 0) > 0
            Button(
                onClick = {
                    val w = weightInput.toDoubleOrNull() ?: 0.0
                    val r = repsInput.toIntOrNull() ?: 0
                    onAddSet(w, r)
                    weightInput = ""
                },
                enabled = canAdd,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.workout_add_set))
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.HeaderCell(label: String, weight: Float) {
    Text(
        label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.weight(weight)
    )
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.BodyCell(value: String, weight: Float) {
    Text(
        value,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.weight(weight)
    )
}
