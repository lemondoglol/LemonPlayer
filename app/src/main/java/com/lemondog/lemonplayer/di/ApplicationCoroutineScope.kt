package com.lemondog.lemonplayer.di

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

class ApplicationCoroutineScope(context: CoroutineContext) : CoroutineScope {
    override val coroutineContext: CoroutineContext = context
}
