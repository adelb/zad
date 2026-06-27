package com.zad.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zad.app.R
import com.zad.app.ui.ZadViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(
    vm: ZadViewModel,
    routineId: Long,
    onBack: () -> Unit,
    onStartSession: (Long) -> Unit,
    onEdit: (Long) -> Unit = {}
) {
    val exercises by vm.exercisesForRoutine(routineId)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val routines by vm.routines.collectAsStateWithLifecycle()
    val routine = routines.firstOrNull { it.id == routineId }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(routine?.nameAr ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(routineId) }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.profile_edit))
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 1.dp) {
                Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (routine?.isPreset == false) {
                        OutlinedButton(
                            onClick = { vm.deleteCustomRoutine(routineId); onBack() }
                        ) { Text(stringResource(R.string.delete)) }
                    }
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = {
                            scope.launch {
                                val sid = vm.startSession(routineId, routine?.nameAr ?: "")
                                onStartSession(sid)
                            }
                        },
                        modifier = Modifier.weight(2f),
                        enabled = exercises.isNotEmpty()
                    ) {
                        Text(stringResource(R.string.workout_start))
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(exercises, key = { it.id }) { ex ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(ex.nameAr, style = MaterialTheme.typography.titleMedium)
                        }
                        Text(
                            "${ex.targetSets} × ${ex.targetReps}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}
