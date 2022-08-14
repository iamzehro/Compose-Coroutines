package com.zehro.coroutinescourse

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.zehro.coroutinescourse.ui.theme.CoroutinesCourseTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL

class MainActivity : ComponentActivity() {

    private val IMAGE_URL =
        "https://upload.wikimedia.org/wikipedia/commons/1/1c/Homer_British_Museum.jpg"
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var imageBM = mutableStateOf(ImageBitmap(200, 200))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coroutineScope.launch {
            val originalDeferred = coroutineScope.async(Dispatchers.IO) {
                getOriginalBitmap()
            }
            val originalBitmap = originalDeferred.await()

            imageBM.value = originalBitmap.asImageBitmap()
        }

        setContent {
            CoroutinesCourseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Image(
                        bitmap = imageBM.value,
                        contentDescription = null
                    )
                }
            }
        }
    }

    private fun getOriginalBitmap(): Bitmap =
        URL(IMAGE_URL).openStream().use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }

}
