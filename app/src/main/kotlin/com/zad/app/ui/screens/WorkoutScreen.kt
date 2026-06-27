package com.zad.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zad.app.R
import com.zad.app.data.Routine
import com.zad.app.ui.ZadViewModel

@Composable
fun WorkoutScreen(
    vm: ZadViewModel,
    onOpenRoutine: (Long) -> Unit,
    onCreateRoutine: () -> Unit
) {
    val routines by vm.routines.collectAsStateWithLifecycle()
    val sessions by vm.recentSessions.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.workout_title), style = MaterialTheme.typography.displaySmall)
            Spacer(Modifier.weight(1f))
            FilledTonalIconButton(onClick = onCreateRoutine) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.workout_new))
            }
        }

        Spacer(Modifier.height(20.dp))

        val presets = routines.filter { it.isPreset }
        val custom  = routines.filter { !it.isPreset }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (custom.isNotEmpty()) {
                item {
                    SectionHeader(stringResource(R.string.workout_custom))
                }
                items(custom, key = { "c-${it.id}" }) { r -> RoutineCard(r, onOpenRoutine) }
                item { Spacer(Modifier.height(8.dp)) }
            }
            item { SectionHeader(stringResource(R.string.workout_preset)) }
            items(presets, key = { "p-${it.id}" }) { r -> RoutineCard(r, onOpenRoutine) }

            item { Spacer(Modifier.height(16.dp)) }
            item { SectionHeader(stringResource(R.string.workout_recent)) }
            if (sessions.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.workout_no_sessions),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(sessions, key = { it.id }) { s ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(s.routineNameAr, style = MaterialTheme.typography.titleMedium)
                            Text(
                                s.dayKey,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun RoutineCard(r: Routine, onOpen: (Long) -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen(r.id) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(r.nameAr, style = MaterialTheme.typography.titleMedium)
                if (r.descriptionAr.isNotBlank()) {
                    Text(
                        r.descriptionAr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
