package com.zad.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zad.app.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zad.app.data.Exercise
import com.zad.app.data.ExerciseCatalog
import com.zad.app.data.MuscleGroup
import com.zad.app.ui.ZadViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRoutineScreen(
    vm: ZadViewModel,
    onDone: () -> Unit,
    onBack: () -> Unit,
    editRoutineId: Long? = null
) {
    val isEdit = editRoutineId != null
    val routines by vm.routines.collectAsStateWithLifecycle()
    val existing = remember(routines, editRoutineId) {
        editRoutineId?.let { id -> routines.firstOrNull { it.id == id } }
    }

    var name by remember(existing) { mutableStateOf(existing?.nameAr ?: "") }
    var description by remember(existing) { mutableStateOf(existing?.descriptionAr ?: "") }
    val picked = remember { mutableStateListOf<Triple<String, Int, Int>>() }

    // When editing, preload exercises once
    LaunchedEffect(editRoutineId) {
        if (editRoutineId != null) {
            val exs = vm.exercisesForRoutine(editRoutineId).firstOrNull() ?: emptyList()
            picked.clear()
            picked.addAll(exs.map { Triple(it.exerciseId, it.targetSets, it.targetReps) })
        }
    }

    var pickerOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) stringResource(R.string.profile_edit) else stringResource(R.string.workout_new)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 1.dp) {
                Row(Modifier.padding(16.dp)) {
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = {
                            scope.launch {
                                if (isEdit && editRoutineId != null) {
                                    vm.updateRoutineExercises(editRoutineId, picked.toList())
                                } else {
                                    vm.createCustomRoutine(name.ifBlank { "روتيني" }, description, picked.toList())
                                }
                                onDone()
                            }
                        },
                        enabled = picked.isNotEmpty() && (isEdit || name.isNotBlank()),
                        modifier = Modifier.weight(2f)
                    ) { Text(stringResource(R.string.workout_save_routine)) }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.workout_routine_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("وصف") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.workout_pick_exercise),
                    style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                FilledTonalIconButton(onClick = { pickerOpen = true }) {
                    Icon(Icons.Default.Add, null)
                }
            }
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(picked.size, key = { picked[it].first + "-$it" }) { i ->
                    val (exId, sets, reps) = picked[i]
                    val ex = ExerciseCatalog.byId(exId)
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(ex?.nameAr ?: exId, modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium)
                            Stepper(
                                value = sets, onUp = { picked[i] = picked[i].copy(second = sets + 1) },
                                onDown = { if (sets > 1) picked[i] = picked[i].copy(second = sets - 1) },
                                suffix = " مج"
                            )
                            Spacer(Modifier.width(8.dp))
                            Stepper(
                                value = reps,
                                onUp = { picked[i] = picked[i].copy(third = reps + 1) },
                                onDown = { if (reps > 1) picked[i] = picked[i].copy(third = reps - 1) },
                                suffix = " ع"
                            )
                            Spacer(Modifier.width(4.dp))
                            IconButton(onClick = { picked.removeAt(i) }) {
                                Icon(Icons.Default.Remove, null, tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (pickerOpen) {
        ExercisePickerSheet(
            onDismiss = { pickerOpen = false },
            onPick = { ex ->
                picked.add(Triple(ex.id, 3, 10))
                pickerOpen = false
            }
        )
    }
}

@Composable
private fun Stepper(value: Int, onUp: () -> Unit, onDown: () -> Unit, suffix: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDown, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp))
            }
            Text("$value$suffix",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 6.dp))
            IconButton(onClick = onUp, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePickerSheet(onDismiss: () -> Unit, onPick: (Exercise) -> Unit) {
    var query by remember { mutableStateOf("") }
    var selectedMuscle by remember { mutableStateOf<MuscleGroup?>(null) }
    val all = remember { ExerciseCatalog.ALL }
    val filtered = remember(query, selectedMuscle) {
        all.filter { ex ->
            val matchesMuscle = selectedMuscle == null || ex.muscleGroup == selectedMuscle
            val matchesQuery = query.isBlank() ||
                ex.nameAr.contains(query.trim()) ||
                ex.id.contains(query.trim(), true)
            matchesMuscle && matchesQuery
        }
    }
    val groups = remember(filtered) { filtered.groupBy { it.muscleGroup } }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Text(
                stringResource(R.string.workout_pick_exercise),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.picker_search)) },
                singleLine = true
            )
            Spacer(Modifier.height(10.dp))

            // muscle filter chips — scroll horizontally
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    androidx.compose.material3.FilterChip(
                        selected = selectedMuscle == null,
                        onClick = { selectedMuscle = null },
                        label = { Text("الكل") }
                    )
                }
                items(MuscleGroup.values().toList()) { g ->
                    androidx.compose.material3.FilterChip(
                        selected = selectedMuscle == g,
                        onClick = { selectedMuscle = if (selectedMuscle == g) null else g },
                        label = { Text(g.arabicLabel) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 520.dp)) {
                groups.forEach { (group, exs) ->
                    item(key = "h-${group.name}") {
                        Text(
                            group.arabicLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }
                    items(exs, key = { it.id }) { ex ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPick(ex) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(ex.nameAr, style = MaterialTheme.typography.titleMedium)
                        }
                        Divider()
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
