package com.github.arhor.simple.expense.tracker.util

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.internal.bytebuddy.utility.RandomString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Path

@Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
internal class IOUtilsTest {

    @TempDir
    private lateinit var tempDir: Path

    @Test
    fun `should asynchronously read file larger than allocated byte buffer using coroutine`() {
        // given
        val capacity = 16

        val text = RandomString.make(capacity * 5)
        val path = tempDir.resolve("test-file.txt").also { it.toFile().writeText(text) }

        // when
        var result = ByteArray(0)

        AsynchronousFileChannel.open(path).use { channel ->
            val bytes = ByteBuffer.allocate(capacity)
            val array = bytes.array()

            runBlocking {
                var num = 0
                var pos = 0L
                while (channel.readAsync(bytes, pos).also { num = it; pos += it } != -1) {
                    result += array.copyOf(num)
                    bytes.clear()
                }
            }
        }

        // then
        assertThat(result)
            .asString()
            .isEqualTo(text)
    }
}
