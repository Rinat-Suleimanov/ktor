/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.engine.cio

import io.ktor.util.cio.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.network.sockets.Socket
import io.ktor.network.util.*

internal class ConnectionFactory(
    private val selector: SelectorManager,
    maxConnectionsCount: Int
) {
    private val semaphore = Semaphore(maxConnectionsCount)

    suspend fun connect(address: NetworkAddress): Socket {
        semaphore.enter()
        return try {
            aSocket(selector).tcpNoDelay().tcp().connect(address)
        } catch (cause: Throwable) {
            // a failure or cancellation
            semaphore.leave()
            throw cause
        }
    }

    fun release() {
        semaphore.leave()
    }
}
