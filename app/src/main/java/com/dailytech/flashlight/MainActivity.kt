package com.dailytech.flashlight

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dailytech.flashlight.FlashManager.DEFAULT_SELECTED_POSITION
import com.dailytech.flashlight.ui.theme.FlashlightTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import timber.log.Timber

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        setContent {
            RootComposeView()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
private fun RootComposeView() {

    FlashlightTheme {
        val context = LocalContext.current

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(text = stringResource(id = R.string.app_name))
                    },
                    actions = {

                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND)
                                shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                shareIntent.type = "text/plain"
                                shareIntent.putExtra(
                                    Intent.EXTRA_TEXT,
                                    context.getString(
                                        R.string.share_message,
                                        getPlayStoreDeepLink(context)
                                    )
                                )
                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        context.getString(R.string.share)
                                    )
                                )
                            }
                        ) {
                            Icon(Icons.Rounded.Share, contentDescription = null)
                        }
                        IconButton(
                            //TODO handle this
                            onClick = { Timber.d("buttonClicked") }
                        ) {
                            Icon(Icons.Rounded.Settings, contentDescription = null)
                        }

                    }
                )
            }
        ) {
            MainContainer(Modifier.padding(it))
        }
    }
}

@Composable
fun MainContainer(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(paddingValues = PaddingValues(bottom = 20.dp))
    ) {

        AdmobBanner(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
        var isOn by remember { mutableStateOf(false) }
        Column {
            if (true) {//todo add remoteConfig condition
                AdmobBanner(modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FlashManager.throbbingList.forEach {
                    Text(
                        text = it.name,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            var sliderPosition by remember { mutableFloatStateOf(DEFAULT_SELECTED_POSITION.toFloat()) }
            Slider(
                value = sliderPosition,
                onValueChange = {
                    Timber.d("slider position: $it")
                    sliderPosition = it
                    FlashManager.selectedThrobbingItem = FlashManager.throbbingList[it.toInt()]
                    isOn = true
                    FlashManager.toggleFlash(context, isOn = true, coroutineScope)
                },
                steps = FlashManager.throbbingList.size - 2,
                valueRange = 0f..(FlashManager.throbbingList.size.toFloat() - 1)
            )
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 80.dp),
            onClick = {
                isOn = !isOn
                FlashManager.toggleFlash(context, isOn = isOn, coroutineScope)
            }
        ) {

            Icon(
                modifier = Modifier.size(100.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_power_off),
                contentDescription = null
            )
        }
    }

}

@Composable
fun AdmobBanner(modifier: Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            // on below line specifying ad view.
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = context.getString(R.string.bottom_banner)
                // calling load ad to load our ad.
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
