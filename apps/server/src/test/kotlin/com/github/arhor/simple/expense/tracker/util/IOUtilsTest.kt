package com.github.arhor.simple.expense.tracker.util

import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.runBlocking
import net.bytebuddy.utility.RandomString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

internal class IOUtilsTest {

    @TempDir
    private lateinit var tempDir: Path

    @Test
    fun `should asynchronously read file larger than defined chunk size using coroutine`() {
        // given
        val capacity = 256

        val text = RandomString.make(capacity * 5)
        val path = tempDir.resolve("test-file.txt").also { it.toFile().writeText(text) }

        // when
        val result = runBlocking { path.chunkedFlow(chunkSize = capacity).reduce(ByteArray::plus) }

        // then
        assertThat(result)
            .asString()
            .isEqualTo(text)
    }
}
