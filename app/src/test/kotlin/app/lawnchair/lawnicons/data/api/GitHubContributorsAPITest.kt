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

package app.lawnchair.lawnicons.data.api

import app.lawnchair.lawnicons.data.kotlinxJson
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

class GitHubContributorsAPITest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: GitHubContributorsAPI

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(kotlinxJson.asConverterFactory("application/json".toMediaType()))
            .build()
            .create()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getContributors returns correct data`() = runTest {
        val responseJson = """
            [
              {
                "id": 1,
                "login": "user1",
                "avatar_url": "https://example.com/avatar1",
                "html_url": "https://example.com/user1",
                "contributions": 10
              },
              {
                "id": 2,
                "login": "user2",
                "avatar_url": "https://example.com/avatar2",
                "html_url": "https://example.com/user2",
                "contributions": 20
              }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(responseJson))

        val contributors = api.getContributors()

        val request = mockWebServer.takeRequest()
        assertEquals("/repos/LawnchairLauncher/lawnicons/contributors", request.path)

        assertEquals(2, contributors.size)
        assertEquals(1L, contributors[0].id)
        assertEquals("user1", contributors[0].login)
        assertEquals("https://example.com/avatar1", contributors[0].avatarUrl)
        assertEquals("https://example.com/user1", contributors[0].htmlUrl)
        assertEquals(10, contributors[0].contributions)
    }
}
