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

    private val IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/1/1c/Homer_British_Museum.jpg"
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var imageBitmapState = mutableStateOf(ImageBitmap(200, 200))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coroutineScope.launch {
            // Get image bitmap from a URL
            val originalDeferred = coroutineScope.async(Dispatchers.IO) {
                getOriginalBitmap()
            }
            // Await result of coroutine
            val originalBitmap = originalDeferred.await()

            // assign bitmap to state holder
            imageBitmapState.value = originalBitmap.asImageBitmap()

            // Apply greyscale filter to the Bitmap image
            val filteredDeferred = coroutineScope.async(Dispatchers.Default) {
                applyImageFilter(originalBitmap)
            }
            // Await result of coroutine
            val filteredBitmap = filteredDeferred.await()

            // Assign filtered bitmap to state holder
            imageBitmapState.value = filteredBitmap.asImageBitmap()
        }

        setContent {
            CoroutinesCourseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Image is initially empty and only shows image after coroutine has loaded bitmap from url
                    Image(
                        bitmap = imageBitmapState.value,
                        contentDescription = null
                    )
                }
            }
        }
    }

    private fun applyImageFilter(originalBitmap: Bitmap): Bitmap {
        return Filter.apply(originalBitmap)
    }

    private fun getOriginalBitmap(): Bitmap =
        URL(IMAGE_URL).openStream().use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
}
