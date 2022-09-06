@file:Suppress("UNUSED")

package com.github.arhor.simple.expense.tracker.util

import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun AsynchronousFileChannel.readAsync(
    buffer: ByteBuffer,
    position: Long,
): Int = suspendCoroutine { continuation ->
    read(buffer, position, Unit, object : CompletionHandler<Int, Unit> {

        override fun completed(bytesRead: Int, attachment: Unit) {
            continuation.resume(bytesRead)
        }

        override fun failed(exception: Throwable, attachment: Unit) {
            continuation.resumeWithException(exception)
        }
    })
}
