package com.zad.app.nav

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zad.app.R
import com.zad.app.ml.PortionEstimator
import com.zad.app.ml.pixelAreaToCm2
import com.zad.app.ui.ZadViewModel
import androidx.compose.runtime.collectAsState
import com.zad.app.ui.screens.CameraScreen
import com.zad.app.ui.screens.HistoryScreen
import com.zad.app.ui.screens.NewRoutineScreen
import com.zad.app.ui.screens.OnboardingScreen
import com.zad.app.ui.screens.ProfileScreen
import com.zad.app.ui.screens.ResultScreen
import com.zad.app.ui.screens.RoutineDetailScreen
import com.zad.app.ui.screens.ScaleScreen
import com.zad.app.ui.screens.SessionLoggerScreen
import com.zad.app.ui.screens.SplashScreen
import com.zad.app.ui.screens.TodayScreen
import com.zad.app.ui.screens.WaterScreen
import com.zad.app.ui.screens.WeightScreen
import com.zad.app.ui.screens.WorkoutScreen
import com.zad.app.vision.computeScaleMmPerPx
import java.net.URLDecoder

@Composable
fun ZadNavRoot() {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val vm: ZadViewModel = viewModel()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            val showTabs = currentRoute in listOf(
                Routes.TODAY, Routes.HISTORY, Routes.CAPTURE, Routes.WORKOUT
            )
            if (showTabs) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                    NavigationBarItem(
                        selected = currentRoute == Routes.TODAY,
                        onClick = { navigateTab(nav, Routes.TODAY) },
                        icon = { Icon(Icons.Default.Today, null) },
                        label = { Text(stringResource(R.string.tab_today)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.WORKOUT,
                        onClick = { navigateTab(nav, Routes.WORKOUT) },
                        icon = { Icon(Icons.Default.FitnessCenter, null) },
                        label = { Text(stringResource(R.string.tab_workout)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.CAPTURE,
                        onClick = { navigateTab(nav, Routes.CAPTURE) },
                        icon = { Icon(Icons.Default.PhotoCamera, null) },
                        label = { Text(stringResource(R.string.tab_capture)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.HISTORY,
                        onClick = { navigateTab(nav, Routes.HISTORY) },
                        icon = { Icon(Icons.Default.History, null) },
                        label = { Text(stringResource(R.string.tab_history)) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.SPLASH) {
                val onboarded by vm.onboarded.collectAsState()
                SplashScreen(onDone = {
                    val next = if (onboarded == true) Routes.TODAY else Routes.ONBOARDING
                    nav.navigate(next) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                })
            }
            composable(Routes.ONBOARDING) {
                OnboardingScreen(onFinish = { p ->
                    vm.saveProfile(p)
                    nav.navigate(Routes.TODAY) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                })
            }
            composable(Routes.TODAY) {
                TodayScreen(
                    vm = vm,
                    onCapture = { nav.navigate(Routes.CAPTURE) },
                    onOpenProfile = { nav.navigate(Routes.PROFILE) },
                    onOpenWater = { nav.navigate(Routes.WATER) },
                    onOpenWeight = { nav.navigate(Routes.WEIGHT) }
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(vm = vm, onBack = { nav.popBackStack() })
            }
            composable(Routes.WEIGHT) { WeightScreen(vm = vm, onBack = { nav.popBackStack() }) }
            composable(Routes.WATER) { WaterScreen(vm = vm, onBack = { nav.popBackStack() }) }
            composable(Routes.WORKOUT) {
                WorkoutScreen(
                    vm = vm,
                    onOpenRoutine = { id -> nav.navigate(Routes.routine(id)) },
                    onCreateRoutine = { nav.navigate(Routes.ROUTINE_NEW) }
                )
            }
            composable(Routes.ROUTINE_NEW) {
                NewRoutineScreen(
                    vm = vm,
                    onDone = { nav.popBackStack() },
                    onBack = { nav.popBackStack() }
                )
            }
            composable(
                Routes.ROUTINE_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("id") ?: return@composable
                RoutineDetailScreen(
                    vm = vm,
                    routineId = id,
                    onBack = { nav.popBackStack() },
                    onStartSession = { sid ->
                        nav.navigate(Routes.session(sid, id)) {
                            popUpTo(Routes.WORKOUT)
                        }
                    }
                )
            }
            composable(
                Routes.SESSION,
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.LongType },
                    navArgument("routineId") { type = NavType.LongType }
                )
            ) { entry ->
                val sid = entry.arguments?.getLong("sessionId") ?: return@composable
                val rid = entry.arguments?.getLong("routineId") ?: return@composable
                SessionLoggerScreen(
                    vm = vm,
                    sessionId = sid,
                    routineId = rid,
                    onFinish = { nav.popBackStack(Routes.WORKOUT, inclusive = false) }
                )
            }
            composable(Routes.HISTORY) { HistoryScreen(vm = vm) }
            composable(Routes.CAPTURE) {
                CameraScreen(
                    onPhotoCaptured = { path ->
                        vm.onPhotoReady(path)
                        nav.navigate(Routes.RESULT)
                    },
                    onPickedFromGallery = { path ->
                        vm.onPhotoReady(path)
                        nav.navigate(Routes.RESULT)
                    }
                )
            }
            composable(Routes.RESULT) {
                ResultScreen(
                    vm = vm,
                    onOpenScale = { path -> nav.navigate(Routes.scale(path)) },
                    onSaved = {
                        nav.popBackStack(Routes.TODAY, inclusive = false)
                    },
                    onDiscard = {
                        vm.reset()
                        nav.popBackStack(Routes.TODAY, inclusive = false)
                    }
                )
            }
            composable(
                Routes.SCALE,
                arguments = listOf(navArgument("path") { type = NavType.StringType })
            ) { entry ->
                val raw = entry.arguments?.getString("path") ?: return@composable
                val path = URLDecoder.decode(raw, "UTF-8")
                ScaleScreen(
                    photoPath = path,
                    onCancel = { nav.popBackStack() },
                    onDone = { corners, plate ->
                        val mmPerPx = computeScaleMmPerPx(corners)
                        if (mmPerPx != null) {
                            val cm2 = pixelAreaToCm2(plate.width, plate.height, mmPerPx)
                            val (g, _) = PortionEstimator.fromArea(vm.scan.value.dish, cm2)
                            vm.setGrams(g)
                        }
                        nav.popBackStack()
                    }
                )
            }
        }
    }
}

private fun navigateTab(nav: androidx.navigation.NavController, route: String) {
    nav.navigate(route) {
        popUpTo(nav.graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
