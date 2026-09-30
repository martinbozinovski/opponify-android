package com.opponify.testing

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object TestDispatchers {
    val default: CoroutineDispatcher = Dispatchers.Default
}
