/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.tests.utils.tests

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlin.test.*

internal fun Application.cookiesTest() {
    routing {
        route("cookies") {
            get("/") {
                val cookie = Cookie("hello-cookie", "my-awesome-value", domain = "localhost")
                context.response.cookies.append(cookie)

                context.respond("Done")
            }
            get("/update-user-id") {
                val id = context.request.cookies["id"]?.toInt() ?: let {
                    context.response.status(HttpStatusCode.Forbidden)
                    context.respondText("Forbidden")
                    return@get
                }

                with(context.response.cookies) {
                    append(Cookie("id", (id + 1).toString(), domain = "localhost", path = "/"))
                    append(Cookie("user", "ktor", domain = "localhost", path = "/"))
                }

                context.respond("Done")
            }
            get("/multiple") {
                val cookies = context.request.cookies
                val first = cookies["first"] ?: fail()
                val second = cookies["second"] ?: fail()

                assertEquals("first-cookie", first)
                assertEquals("second-cookie", second)
                context.respond("Multiple done")
            }
            get("/withPath") {
                val cookie = Cookie("marker", "value", path = "/withPath/")
                context.response.cookies.append(cookie)
                context.respond("OK")
            }
            get("/withPath/something") {
                val cookies = context.request.cookies
                if (cookies["marker"] == "value") {
                    context.respond("OK")
                } else {
                    context.respond(HttpStatusCode.BadRequest)
                }
            }
            get("/foo") {
                val cookie = Cookie("foo", "bar")
                context.response.cookies.append(cookie)

                call.respond("OK")
            }
            get("/FOO") {
                assertTrue(call.request.cookies.rawCookies.isEmpty())
                call.respond("OK")
            }
            get("/expire") {
                call.request.cookies.rawCookies.forEach { (name, _) ->
                    call.response.cookies.appendExpired(name, path = "/")
                }
                call.respond("OK")
            }
        }
    }
}
