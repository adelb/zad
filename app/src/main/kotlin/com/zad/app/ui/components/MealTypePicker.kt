package com.zad.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zad.app.R
import com.zad.app.data.MealType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealTypePickerSheet(onDismiss: () -> Unit, onPick: (MealType) -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
            Text(
                stringResource(R.string.result_add_question),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(16.dp))
            MealOption(stringResource(R.string.result_add_to_breakfast)) { onPick(MealType.BREAKFAST) }
            Spacer(Modifier.height(8.dp))
            MealOption(stringResource(R.string.result_add_to_lunch)) { onPick(MealType.LUNCH) }
            Spacer(Modifier.height(8.dp))
            MealOption(stringResource(R.string.result_add_to_dinner)) { onPick(MealType.DINNER) }
            Spacer(Modifier.height(8.dp))
            MealOption(stringResource(R.string.result_add_to_snack)) { onPick(MealType.SNACK) }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MealOption(label: String, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 18.dp)
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
    }
}
