package com.zad.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.zad.app.R
import com.zad.app.data.MealType
import com.zad.app.ml.FoodClassifier
import com.zad.app.ui.ZadViewModel
import com.zad.app.ui.components.DishPickerSheet
import com.zad.app.ui.components.MealTypePickerSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    vm: ZadViewModel,
    onOpenScale: (photoPath: String) -> Unit,
    onSaved: () -> Unit,
    onDiscard: () -> Unit
) {
    val state by vm.scan.collectAsStateWithLifecycle()
    var showDishPicker by remember { mutableStateOf(false) }
    var showMealPicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.savedSuccessfully) {
        if (state.savedSuccessfully) {
            onSaved()
            vm.reset()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.result_title), style = MaterialTheme.typography.headlineMedium) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            state.photoPath?.let { path ->
                AsyncImage(
                    model = path,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }

            if (state.processing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Dish name + confidence hint
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.result_dish), style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text(state.dish.nameAr, style = MaterialTheme.typography.displaySmall)
                    val pred = state.prediction
                    if (pred != null && pred.confidence < FoodClassifier.LOW_CONFIDENCE_THRESHOLD) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            stringResource(R.string.result_low_confidence),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = { showDishPicker = true }) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.result_change_dish))
                    }
                }
            }

            // Weight + Calories
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    title = stringResource(R.string.result_weight),
                    value = "${state.grams}",
                    unit = stringResource(R.string.result_grams),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = stringResource(R.string.result_calories),
                    value = "${state.calories}",
                    unit = stringResource(R.string.result_kcal),
                    modifier = Modifier.weight(1f)
                )
            }

            Column(Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.result_adjust_grams),
                    style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = state.grams.toFloat(),
                    onValueChange = { vm.setGrams(it.toInt()) },
                    valueRange = 20f..1500f,
                    steps = 0
                )
            }

            state.photoPath?.let { path ->
                OutlinedButton(
                    onClick = { onOpenScale(path) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AspectRatio, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("قياس دقيق بالبطاقة")
                }
            }

            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onDiscard, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.result_discard))
                }
                Button(onClick = { showMealPicker = true }, modifier = Modifier.weight(2f)) {
                    Text(stringResource(R.string.result_add_question))
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }

    if (showDishPicker) {
        DishPickerSheet(
            onDismiss = { showDishPicker = false },
            onPick = { dish -> vm.pickDish(dish.id); showDishPicker = false }
        )
    }
    if (showMealPicker) {
        MealTypePickerSheet(
            onDismiss = { showMealPicker = false },
            onPick = { type: MealType ->
                vm.addToMeal(type)
                showMealPicker = false
            }
        )
    }
}

@Composable
private fun StatCard(title: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, style = MaterialTheme.typography.displayMedium)
                Spacer(Modifier.width(6.dp))
                Text(unit, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
