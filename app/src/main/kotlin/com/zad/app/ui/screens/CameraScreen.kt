package com.zad.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.zad.app.R
import com.zad.app.util.BitmapUtils
import java.io.File
import java.util.concurrent.Executor

@Composable
fun CameraScreen(
    onPhotoCaptured: (path: String) -> Unit,
    onPickedFromGallery: (path: String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCamPerm by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCamPerm = granted }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val copy = copyUriToCache(context, uri)
            if (copy != null) onPickedFromGallery(copy.absolutePath)
        }
    }

    if (!hasCamPerm) {
        CameraPermissionPrompt(onGrant = { permLauncher.launch(Manifest.permission.CAMERA) })
        return
    }

    val imageCapture = remember { ImageCapture.Builder().build() }
    val executor: Executor = remember { ContextCompat.getMainExecutor(context) }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                val providerFuture = ProcessCameraProvider.getInstance(ctx)
                providerFuture.addListener({
                    val provider = providerFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                    )
                }, executor)
                previewView
            }
        )

        // Top hint card
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            color = Color(0xCC1E1A14),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(R.string.camera_hint),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                textAlign = TextAlign.Center
            )
        }

        // Bottom controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                Icon(
                    Icons.Default.PhotoLibrary,
                    contentDescription = stringResource(R.string.camera_picker),
                    tint = Color.White
                )
            }

            ShutterButton(onClick = {
                val file = BitmapUtils.newCaptureFile(context)
                val outOpts = ImageCapture.OutputFileOptions.Builder(file).build()
                imageCapture.takePicture(
                    outOpts,
                    executor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                            onPhotoCaptured(file.absolutePath)
                        }
                        override fun onError(exc: ImageCaptureException) { /* surface via snackbar in caller */ }
                    }
                )
            })

            Spacer(Modifier.size(48.dp))
        }
    }
}

@Composable
private fun ShutterButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .background(Color.White.copy(alpha = 0.25f), shape = RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(50),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White)
        ) { Spacer(Modifier.size(36.dp)) }
    }
}

@Composable
private fun CameraPermissionPrompt(onGrant: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.camera_permission_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.camera_permission_body),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onGrant) { Text(stringResource(R.string.camera_permission_grant)) }
    }
}

private fun copyUriToCache(context: android.content.Context, uri: Uri): File? = runCatching {
    val out = BitmapUtils.newCaptureFile(context)
    context.contentResolver.openInputStream(uri)?.use { input ->
        out.outputStream().use { input.copyTo(it) }
    }
    out
}.getOrNull()
