package com.pernasA.multiplayerClocks.android.view

import android.content.Intent
import android.net.Uri

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.pernasA.multiplayerClocks.android.utils.BluePrimary
import com.pernasA.multiplayerClocks.android.utils.ButtonPrimaryContainer
import com.pernasA.multiplayerClocks.android.utils.ButtonTertiary
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.BUTTON_HOME_TEXT_SIZE
import com.pernasA.multiplayerClocks.android.utils.Constants.Companion.TITLE_TEXT_SIZE
import com.pernasA.multiplayerClocks.android.utils.MyClocksAppTheme
import com.pernasA.multiplayerClocks.android.viewModel.SharedViewModel
import com.pernasA.multiplayerclock.android.R

@Composable
fun HomePage(
    selectPlayersOnClick: () -> Unit,
    loadGameOnClick: () -> Unit,
    isLoading: Boolean = false,
    sharedViewModel: SharedViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.image_clocks_colour),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.7f
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    HomePageInit(
                        selectPlayersOnClick,
                        loadGameOnClick,
                        sharedViewModel
                    )
                }
            }
        }

        ToolsButtonsBottom(sharedViewModel, sharedViewModel.getSoundsController().soundsEnabled)
    }
}

@Composable
fun HomePageInit(
    selectPlayersOnClick: () -> Unit,
    loadGameOnClick: () -> Unit,
    sharedViewModel: SharedViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        ColumnTitleAndSubtitle(
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = 8.dp, end = 8.dp)
        )

        Column (
            Modifier
                .width(250.dp)
                .padding(top = 130.dp),
            verticalArrangement = Arrangement.Center
        ) {
            MyFilledButton(
                selectPlayersOnClick,
                R.string.start_game,
                Modifier.padding(top = 15.dp),
                sharedViewModel
            )
            MyFilledButton(
                loadGameOnClick,
                R.string.load_previous_game,
                Modifier.padding(top = 40.dp),
                sharedViewModel
            )
        }
    }
}

@Composable
fun ColumnTitleAndSubtitle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 150.dp, bottom = 16.dp)
            .background(
                color = Color.White.copy(alpha = 0.8f),
                shape = RoundedCornerShape(80.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.title_main_page),
                style = TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Serif,
                    fontSize = 65.sp,
                    lineHeight = 65.sp,
                    letterSpacing = 6.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
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
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
    }
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
        modifier.fillMaxWidth().height(100.dp),
        border = BorderStroke(2.5.dp, ButtonPrimaryContainer),
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
fun ToolsButtonsBottom(sharedViewModel: SharedViewModel, soundsEnabled: MutableState<Boolean>) {
    val context = LocalContext.current
    val shareText = stringResource(id = R.string.button_share_text)
    val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://api.whatsapp.com/send?text=$shareText")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 10.dp, bottom = 10.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        LargeFloatingActionButton(
            onClick = {
                sharedViewModel.getSoundsController().playButtonTickSound()
                context.startActivity(whatsappIntent)
            },
            shape = CircleShape,
            containerColor = ButtonTertiary,
            contentColor = Color.Black,
            modifier = Modifier.size(70.dp)
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = CircleShape
                )
        ) {
            Icon(Icons.Filled.Share, stringResource(R.string.button_share_description))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, bottom = 10.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        LargeFloatingActionButton(
            onClick = {
                sharedViewModel.getSoundsController().toggleSound()
            },
            shape = CircleShape,
            containerColor = ButtonTertiary,
            contentColor = Color.Black,
            modifier = Modifier.size(70.dp)
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = if (soundsEnabled.value) Icons.AutoMirrored.Default.VolumeUp else Icons.AutoMirrored.Default.VolumeOff,
                contentDescription = "Sonido",
                tint = if (soundsEnabled.value) Color.Black else Color.White
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
