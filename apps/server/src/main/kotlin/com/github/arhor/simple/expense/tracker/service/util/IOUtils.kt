@file:Suppress("UNUSED")

package com.github.arhor.simple.expense.tracker.service.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.file.Path
import java.nio.file.StandardOpenOption.READ
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
fun Path.chunkedFlow(chunkSize: Int = DEFAULT_BUFFER_SIZE): Flow<ByteArray> = flow {
    withContext(Dispatchers.IO) {
        AsynchronousFileChannel.open(
            this@chunkedFlow,
            READ,
        )
    }.use { channel ->
        val bytes = ByteBuffer.allocate(chunkSize)
        val array = bytes.array()

        var num = 0
        var pos = 0L
        while (channel.readAsync(buffer = bytes, position = pos).also { num = it; pos += it } != -1) {
            emit(array.copyOf(newSize = num))
            bytes.clear()
        }
    }
}

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
