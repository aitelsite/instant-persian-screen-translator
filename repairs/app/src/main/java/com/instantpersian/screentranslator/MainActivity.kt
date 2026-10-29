package com.instantpersian.screentranslator

import android.app.Activity
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.instantpersian.screentranslator.overlay.FloatingOverlayManager
import com.instantpersian.screentranslator.service.MediaProjectionCaptureService
import com.instantpersian.screentranslator.translation.TranslationRepository
import com.instantpersian.screentranslator.ui.theme.InstantPersianTheme
import com.instantpersian.screentranslator.util.PermissionHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { InstantPersianTheme { MainScreen(intent.getBooleanExtra(EXTRA_REQUEST_OCR, false)) } }
    }

    companion object {
        const val EXTRA_REQUEST_OCR = "extra_request_ocr"
    }
}

@Composable
private fun MainScreen(startOcrOnLaunch: Boolean = false) {
    val context = LocalContext.current
    val captureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            MediaProjectionCaptureService.startCapture(context, result.resultCode, result.data!!)
        } else {
            Toast.makeText(context, "برای OCR باید اجازه ضبط صفحه را بدهید.", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(startOcrOnLaunch) {
        if (startOcrOnLaunch) {
            (context as? Activity)?.intent?.removeExtra(MainActivity.EXTRA_REQUEST_OCR)
            val manager = context.getSystemService(android.content.Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            captureLauncher.launch(manager.createScreenCaptureIntent())
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("مترجم فوری صفحه", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("متن قابل‌خواندن صفحه را با دکمه پنل اعلان‌ها استخراج و به فارسی ترجمه کن.")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("۱. دسترسی‌های لازم", style = MaterialTheme.typography.titleMedium)
                Button(onClick = {
                    context.startActivity(PermissionHelper.getOverlayPermissionIntent(context))
                }, modifier = Modifier.fillMaxWidth()) { Text("فعال‌کردن نمایش روی برنامه‌ها") }
                OutlinedButton(onClick = {
                    context.startActivity(PermissionHelper.getAccessibilitySettingsIntent())
                }, modifier = Modifier.fillMaxWidth()) { Text("فعال‌کردن دسترسی‌پذیری") }
                Text("در تنظیمات دسترسی‌پذیری، سرویس «مترجم فوری صفحه» را روشن کن.")
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("۲. ترجمه متن داخل تصویر", style = MaterialTheme.typography.titleMedium)
                Text("برای OCR، اندروید قبل از هر نوبت ضبط صفحه اجازه می‌گیرد. موتور OCR فعلی عمدتاً برای متن‌های لاتین است.")
                Button(onClick = {
                    val manager = context.getSystemService(android.content.Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                    captureLauncher.launch(manager.createScreenCaptureIntent())
                }, modifier = Modifier.fillMaxWidth()) { Text("اسکن صفحه با OCR") }
            }
        }
        OutlinedButton(onClick = {
            TranslationRepository.prewarm(context)
            FloatingOverlayManager.show(context, "برای ترجمه از کاشی «ترجمه صفحه» در پنل اعلان‌ها استفاده کن.")
        }, modifier = Modifier.fillMaxWidth()) { Text("نمایش راهنمای سریع") }
        Text("توجه: بعضی برنامه‌ها اجازه خواندن متن یا ضبط صفحه را نمی‌دهند. ترجمه آنلاین ممکن است متن را به سرویس بیرونی ارسال کند.", style = MaterialTheme.typography.bodySmall)
    }
}
