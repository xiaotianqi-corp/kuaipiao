package org.xiaotianqi.kuaipiao

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            module(testing = true)
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Ktor: ${Greeting().greet()}", response.bodyAsText())
    }
}