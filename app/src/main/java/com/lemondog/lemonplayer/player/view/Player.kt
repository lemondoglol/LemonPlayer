package com.lemondog.lemonplayer.player.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.lemondog.lemonplayer.ui.dimens.Size

@Composable
fun PlayerControllerButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    iconSize: Dp = Size.Large,
    boxSize: Dp = Size.LargeP,
    icon: ImageVector,
) {
    Box(
        modifier = modifier
            .size(boxSize)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier.size(iconSize),
            imageVector = icon,
            contentDescription = "",
        )
    }
}