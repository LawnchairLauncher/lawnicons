/*
 * Copyright 2026 Lawnchair Launcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.lawnchair.lawnicons.data.model

import app.lawnchair.lawnicons.data.kotlinxJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ComponentTest {

    @Test
    fun `empty constructor creates blank package and class names`() {
        val component = Component()

        assertEquals("", component.packageName)
        assertEquals("", component.className)
    }

    @Test
    fun `flattenToString returns package and class separated by slash`() {
        val component = Component("com.example", "com.example.MainActivity")

        assertEquals("com.example/com.example.MainActivity", component.flattenToString())
    }

    @Test
    fun `toString returns appfilter component format`() {
        val component = Component("com.example", "com.example.MainActivity")

        assertEquals("ComponentInfo{com.example/com.example.MainActivity}", component.toString())
    }

    @Test
    fun `unflattenFromString parses fully qualified class name`() {
        val parsed = Component.unflattenFromString("com.example/com.example.MainActivity")

        assertEquals(parsed, Component("com.example", "com.example.MainActivity"))
    }

    @Test
    fun `unflattenFromString expands relative class name with package`() {
        val parsed = Component.unflattenFromString("com.example/.MainActivity")

        assertEquals(parsed, Component("com.example", "com.example.MainActivity"))
    }

    @Test
    fun `unflattenFromString returns null when slash is missing`() {
        val parsed = Component.unflattenFromString("com.example.MainActivity")

        assertNull(parsed)
    }

    @Test
    fun `unflattenFromString returns null when class part is missing`() {
        val parsed = Component.unflattenFromString("com.example/")

        assertNull(parsed)
    }

    @Test
    fun `equals and hashCode are based on package and class name`() {
        val a = Component("com.example", "com.example.MainActivity")
        val b = Component("com.example", "com.example.MainActivity")
        val c = Component("com.example", "com.example.SettingsActivity")

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
        assertNotEquals(a, c)
    }

    @Test
    fun `compareTo orders by package then class name`() {
        val unordered = listOf(
            Component("com.beta", "com.beta.Main"),
            Component("com.alpha", "com.alpha.Settings"),
            Component("com.alpha", "com.alpha.Main"),
        )

        val sorted = unordered.sorted()

        assertEquals(Component("com.alpha", "com.alpha.Main"), sorted[0])
        assertEquals(Component("com.alpha", "com.alpha.Settings"), sorted[1])
        assertEquals(Component("com.beta", "com.beta.Main"), sorted[2])
        assertTrue(sorted[0] < sorted[1])
    }

    @Test
    fun `serialization round trip preserves values`() {
        val original = Component("com.example", "com.example.MainActivity")

        val encoded = kotlinxJson.encodeToString(original)
        val decoded = kotlinxJson.decodeFromString<Component>(encoded)

        assertEquals(original, decoded)
    }
}
