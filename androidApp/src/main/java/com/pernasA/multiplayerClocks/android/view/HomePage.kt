package com.pernasA.multiplayerClocks.android.view

import android.content.Intent
import android.net.Uri

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

import com.pernasA.multiplayerClocks.android.utils.BluePrimary
import com.pernasA.multiplayerClocks.android.utils.ButtonPrimaryContainer
import com.pernasA.multiplayerClocks.android.utils.ButtonTertiary
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.BUTTON_HOME_TEXT_SIZE
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.TITLE_TEXT_SIZE
import com.pernasA.multiplayerClocks.android.utils.MyClocksAppTheme
import com.pernasA.multiplayerClocks.android.utils.PrimaryAccent
import com.pernasA.multiplayerClocks.android.viewModel.SharedViewModel
import com.pernasA.multiplayerclock.android.R

@Composable
fun HomePage(
    selectPlayersOnClick: () -> Unit,
    loadGameOnClick: () -> Unit,
    isLoading: Boolean = false,
    sharedViewModel: SharedViewModel
) {
    val showTooltip = remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        if (showTooltip.value) {
                            showTooltip.value = false
                        }
                    }
                },
        ) {
            item {
                HomePageInit(
                    selectPlayersOnClick,
                    loadGameOnClick,
                    showTooltip,
                    sharedViewModel
                )
            }
        }
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                CircularProgressIndicator(
                    color = BluePrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        ToolsButtonsBottom(sharedViewModel, sharedViewModel.getSoundsController().soundsEnabled)
    }
}

@Composable
fun HomePageInit(
    selectPlayersOnClick: () -> Unit,
    loadGameOnClick: () -> Unit,
    showTooltip: MutableState<Boolean>,
    sharedViewModel: SharedViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        RowTitle(
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 8.dp)
        )

        ZoomableImage(
            R.drawable.relojes_triangulo,
            Modifier
                .size(300.dp)
                .padding(top = 20.dp)
                .clip(RoundedCornerShape(32.dp))
                .shadow(100.dp, RoundedCornerShape(32.dp))
                .border(BorderStroke(1.dp, BluePrimary), RoundedCornerShape(32.dp)),
            ContentScale.Crop
        )

        Column (
            Modifier
                .width(250.dp)
                .padding(top = 26.dp)) {
            MyFilledButton(
                selectPlayersOnClick,
                R.string.start_game,
                Modifier.padding(top = 15.dp),
                sharedViewModel
            )
            MyFilledButton(
                loadGameOnClick,
                R.string.load_previous_game,
                Modifier.padding(top = 15.dp),
                sharedViewModel
            )
            //RowRoutesButtons(loadGameOnClick, showTooltip)
        }
    }
}

@Composable
fun RowTitle(modifier: Modifier) {
    Text(
        text = stringResource(R.string.title_main_page),
        style = TextStyle(
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Serif,
            fontSize = 65.sp,
            lineHeight = 65.sp,
            letterSpacing = 6.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
            shadow = Shadow(BluePrimary),
        ),
        modifier = modifier.padding(top = 15.dp)
    )
    Text(
        text = stringResource(R.string.subtitle_main_page),
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            fontSize = TITLE_TEXT_SIZE,
            lineHeight = TITLE_TEXT_SIZE,
            textAlign = TextAlign.Center,
            color = BluePrimary,
            shadow = Shadow(BluePrimary)
        ),
        modifier = modifier.padding(5.dp)
    )
}

@Composable
fun MyFilledButton(
    onClick: () -> Unit,
    buttonText: Int,
    modifier: Modifier,
    sharedViewModel: SharedViewModel,

    ) {
    FilledTonalButton(
        onClick = {
            sharedViewModel.getSoundsController().playButtonTickSound()
            onClick()
                  },
        modifier.fillMaxWidth(),
        border = BorderStroke(0.7.dp, ButtonPrimaryContainer),
    ) {
        Text(
            stringResource(buttonText),
            fontSize = BUTTON_HOME_TEXT_SIZE,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun RowRoutesButtons(
    observationRoutesOnClick: () -> Unit,
    showTooltipState: MutableState<Boolean>,
    sharedViewModel: SharedViewModel
) {
    val showTooltip by showTooltipState
    Row (modifier = Modifier.padding(top = 18.dp)) {
        MyFilledButton(
            observationRoutesOnClick,
            R.string.load_previous_game,
            Modifier
                .padding(end = 5.dp)
                .weight(1F),
            sharedViewModel
        )
        Box(modifier = Modifier.width(5.dp))
        Box(
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(onTap = {
                    showTooltipState.value = !showTooltip
                })
            }
        ) {
            LargeFloatingActionButton(
                onClick = { showTooltipState.value = !showTooltip },
                shape = CircleShape,
                modifier = Modifier.size(45.dp)
            ) {
                Icon(
                    Icons.Filled.Info,
                    stringResource(R.string.button_info),
                    tint = Color.Black,
                    modifier = Modifier.size(33.dp)
                )
            }

            if (showTooltip) {
                Popup(
                    alignment = Alignment.TopEnd,
                    offset = IntOffset(200, -200),
                    properties = PopupProperties(focusable = false)
                ) {
                    Surface(
                        modifier = Modifier.padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = PrimaryAccent,
                        shadowElevation = 4.dp,
                        border = BorderStroke(0.5.dp, Color.White)
                    ) {
                        Text(
                            text = stringResource(R.string.button_info_text),
                            modifier = Modifier.padding(8.dp),
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ToolsButtonsBottom(sharedViewModel: SharedViewModel, soundsEnabled: MutableState<Boolean>) {
    val context = LocalContext.current
    val shareText = stringResource(id = R.string.button_share_text)
    val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://api.whatsapp.com/send?text=$shareText") //TODO: CHECKEAR EL LINK A LA PLAY STORE
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 10.dp),
        contentAlignment = Alignment.BottomEnd // Botón de WhatsApp en la parte inferior derecha
    ) {
        // Botón de WhatsApp
        LargeFloatingActionButton(
            onClick = {
                sharedViewModel.getSoundsController().playButtonTickSound()
                context.startActivity(whatsappIntent)
            },
            shape = CircleShape,
            containerColor = ButtonTertiary,
            contentColor = Color.Black,
            modifier = Modifier.size(60.dp)
        ) {
            Icon(Icons.Filled.Share, stringResource(R.string.button_share_description))
        }
    }

    // Botón de sonido a la izquierda
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, bottom = 10.dp), // Asegúrate de no solaparse con el botón derecho
        contentAlignment = Alignment.BottomStart // Botón a la izquierda
    ) {
        LargeFloatingActionButton(
            onClick = {
                sharedViewModel.getSoundsController().toggleSound()
            },
            shape = CircleShape,
            containerColor = ButtonTertiary,
            contentColor = Color.Black,
            modifier = Modifier.size(60.dp)
        ) {
            Icon(
                imageVector = if (soundsEnabled.value) Icons.AutoMirrored.Default.VolumeUp else Icons.AutoMirrored.Default.VolumeOff,
                contentDescription = "Sonido",
                tint = if (soundsEnabled.value) Color.Green else Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    MyClocksAppTheme (true) {
        HomePage(
            selectPlayersOnClick = {},
            loadGameOnClick = {},
            isLoading = true,
            sharedViewModel = SharedViewModel()
        )
    }
}
