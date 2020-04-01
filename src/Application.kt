@file:Suppress("UNUSED_PARAMETER")

package com.example

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.PartialContent
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.html.respondHtml
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.sessions
import kotlinx.html.*

// Use "gradle -t installDist" to have automatic recompile
// See https://ktor.io/servers/autoreload.html#recompiling-automatically-on-source-changes

// Automatic reloading on class changes is set inside application.conf
// See https://ktor.io/servers/autoreload.html

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(PartialContent) {

    }
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    install(Sessions) {
        cookie<MySession>("SESSION")
    }

/* Did not work for me. Please see  https://ktor.io/quickstart/guides/website.html#using-the-form-authentication

    install(Authentication) {
        form("login") {
            userParamName = "username"
            passwordParamName = "password"
            challenge { call.respond(UnauthorizedResponse()) }
            validate { credentials -> if (credentials.name == credentials.password) UserIdPrincipal(credentials.name) else null }
        }
    }
*/

    routing {
        static("/static") {
            resources("static")
        }
        route("/login") {
            get {
                call.respond(FreeMarkerContent("login.ftl", null))
            }
            post {
                val post = call.receiveParameters()
                val userName = post["username"]
                if (userName != null && userName == post["password"]) {
                    call.sessions.set("SESSION", MySession(userName))
                    //val principal = call.principal<UserIdPrincipal>() ?: error("No principal")
                    call.respondRedirect("/html-dsl", permanent = false)
                } else {
                    call.respond(FreeMarkerContent("login.ftl", mapOf("error" to "Error during login")))
                }
            }
/* Did not work for me. See https://ktor.io/quickstart/guides/website.html#using-the-form-authentication

            authenticate("login") {
                post {
                    val principal = call.principal<UserIdPrincipal>()
                    call.respondRedirect("/", permanent = false)
                }
            }
*/
        }
        get("/") {
            call.respondRedirect("/login", permanent = false)
            // call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }


        get("/html-dsl") {

            val session = call.sessions.get("SESSION") as MySession?
            val data = IndexData(listOf(1, 2, 3))
            call.respondHtml {
                head {
                    link(rel = "stylesheet", href = "static/styles.css")
                }
                body {
                    p {
                        text("Username: ${session?.userName}")
                    }
                    ul {
                        for (item in data.items) {
                            li { +"$item" }
                        }
                    }
                }
            }
        }
    }
}

data class MySession(val userName: String)
data class IndexData(val items: List<Int>)
