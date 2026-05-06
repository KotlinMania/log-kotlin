// port-lint: source src/kv/key.rs
package io.github.kotlinmania.log.kv

import kotlin.test.Test
import kotlin.test.assertEquals

class KeyTest {
    @Test
    fun keyFromString() {
        assertEquals("a key", Key.fromStr("a key").asStr())
    }

    @Test
    fun keyToBorrowed() {
        assertEquals("a key", Key.fromStr("a key").toBorrowedStr())
    }
}
