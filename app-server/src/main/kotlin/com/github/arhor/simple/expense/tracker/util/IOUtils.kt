@file:Suppress("UNUSED")

package com.github.arhor.simple.expense.tracker.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.file.Paths
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun chunked(fileName: String, chunkSize: Int = DEFAULT_BUFFER_SIZE): Flow<Pair<ByteBuffer, Int>> {
    val buffer = ByteBuffer.allocate(chunkSize)

    return AsynchronousFileChannel.open(Paths.get(fileName)).use { channel ->
        flow {
            var numRead: Int

            while (channel.readAsync(buffer).also { numRead = it } != -1) {
                if (numRead > 0) {
                    emit(buffer to numRead)
                }
            }
        }
    }
}

suspend fun AsynchronousFileChannel.readAsync(buffer: ByteBuffer): Int = suspendCoroutine { continuation ->
    read(buffer, 0L, Unit, object : CompletionHandler<Int, Unit> {

        override fun completed(bytesRead: Int, attachment: Unit) {
            continuation.resume(bytesRead)
        }

        override fun failed(exception: Throwable, attachment: Unit) {
            continuation.resumeWithException(exception)
        }
    })
}
