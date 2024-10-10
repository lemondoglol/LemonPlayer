package com.lemondog.lemonplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!viewModel.playerReadyToUse.value) {
            viewModel.initMediaController()
        }
    }

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
    private fun PlayerScreen(
        modifier: Modifier = Modifier,
    ) {
        Column(modifier = modifier) {
            if (viewModel.playerReadyToUse.value) {
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
            Button(onClick = { viewModel.playAudioItem() }) {
                Text(text = "Clieck me")
            }
        }
    }
}
