/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.util.pipeline

import kotlin.coroutines.*
import kotlinx.coroutines.internal.*

internal fun recoverStackTraceBridge(exception: Throwable, continuation: Continuation<*>): Throwable {
    @Suppress("INVISIBLE_MEMBER")
    return recoverStackTrace(exception, continuation).withCause(exception.cause)
}

internal expect fun Throwable.withCause(cause: Throwable?): Throwable
