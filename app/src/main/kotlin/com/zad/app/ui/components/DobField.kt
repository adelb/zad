package com.zad.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zad.app.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DobField(
    value: LocalDate,
    onChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var open by remember { mutableStateOf(false) }
    val df = remember { DateTimeFormatter.ofPattern("d MMMM yyyy") }

    Box(modifier.fillMaxWidth().clickable { open = true }) {
        OutlinedTextField(
            value = value.format(df),
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text(stringResource(R.string.onb_dob)) },
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (open) {
        val initial = value.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        val state = rememberDatePickerState(initialSelectedDateMillis = initial)
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { ms ->
                        val date = Instant.ofEpochMilli(ms).atZone(ZoneId.of("UTC")).toLocalDate()
                        onChange(date)
                    }
                    open = false
                }) { Text(stringResource(R.string.confirm_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { open = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = state, title = {
                Text(
                    stringResource(R.string.dob_select),
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            })
        }
    }
}
