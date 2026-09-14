package com.jorotayo.algorubickrevamped

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jorotayo.algorubickrevamped.ui.theme.AlgorubickTheme
import com.jorotayo.algorubickrevamped.ui.theme.DefaultPreviews
import com.jorotayo.algorubickrevamped.ui.theme.Primary
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ObjectBox.init(this)

        setContent {
            AlgorubickTheme {
                SplashContent()
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_DURATION_MS)
    }

    companion object {
        private const val SPLASH_DURATION_MS = 2500L
    }
}

@Composable
private fun SplashContent() {
    val imageScale = remember { Animatable(0f) }
    val imageAlpha = remember { Animatable(0f) }
    val imageRotation = remember { Animatable(0f) }
    val textOffset = remember { Animatable(120f) }
    val textAlpha = remember { Animatable(0.1f) }
    val logoGrey = Color(0xFFD0CECE)
    val revamped = FontFamily(Font(R.font.revamped, FontWeight.Bold))

    LaunchedEffect(Unit) {
        launch {
            imageScale.animateTo(1f, tween(durationMillis = 2500))
        }
        launch {
            imageAlpha.animateTo(1f, tween(durationMillis = 2500))
        }
        launch {
            imageRotation.animateTo(360f, tween(durationMillis = 2500))
        }
        launch {
            textOffset.animateTo(0f, tween(durationMillis = 500))
        }
        launch {
            textAlpha.animateTo(1f, tween(durationMillis = 500))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.splashimage),
                contentDescription = stringResource(R.string.splash_logo_description),
                modifier = Modifier
                    .padding(top = 100.dp)
                    .fillMaxWidth()
                    .height(400.dp)
                    .scale(imageScale.value)
                    .rotate(imageRotation.value)
                    .alpha(imageAlpha.value),
            )
            Text(
                text = stringResource(R.string.splash_app_name),
                color = logoGrey,
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = revamped,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .offset(y = textOffset.value.dp)
                    .alpha(textAlpha.value),
            )
            Text(
                text = stringResource(R.string.splash_slogan),
                color = logoGrey,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .offset(y = textOffset.value.dp)
                    .alpha(textAlpha.value),
            )
        }
    }
}

@DefaultPreviews
@Composable
private fun SplashContentPreview() {
    AlgorubickTheme {
        SplashContent()
    }
}
