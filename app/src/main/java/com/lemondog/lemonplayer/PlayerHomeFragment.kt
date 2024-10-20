package com.lemondog.lemonplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.ui.PlayerView
import com.lemondog.lemonplayer.player.Media3PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerHomeFragment : Fragment() {

    private val viewModel: Media3PlayerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Scaffold(
                    content = {
                        PlayerScreen(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it),
                        )
                    }
                )
            }
        }
    }

    @Composable
    private fun PlaybackSection(
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

        }
    }

    @Composable
    private fun PlayerControllerSection(
        modifier: Modifier = Modifier,
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Next, Play/Pause, Next
//            Image(
//                imageVector = Icons.Filled.AccountBox,
//            )
        }
    }

    @Composable
    private fun PlayerScreen(
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            viewModel.mediaController?.let {
                AndroidView(
                    factory = { context ->
                        PlayerView(context).also {
                            it.player = viewModel.mediaController
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f),
                )
            }

            Button(onClick = { viewModel.play() }) {
                Text(text = "Play")
            }

            Button(onClick = { viewModel.shufflePlayList() }) {
                if (viewModel.shuffleModeEnabled.value) {
                    Text(text = "Shuffle Mode On")
                } else {
                    Text(text = "Shuffle Mode Off")
                }
            }
        }
    }
}
