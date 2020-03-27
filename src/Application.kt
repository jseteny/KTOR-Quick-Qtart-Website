@file:Suppress("UNUSED_PARAMETER")

package com.example

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UnauthorizedResponse
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.form
import io.ktor.features.PartialContent
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing

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
    install(Authentication) {
        form("login") {
            userParamName = "username"
            passwordParamName = "password"
            challenge { call.respond(UnauthorizedResponse()) }
            validate { credentials -> if (credentials.name == credentials.password) UserIdPrincipal(credentials.name) else null }
        }
    }

    routing {
        route("/login") {
            get {
                call.respond(FreeMarkerContent("login.ftl", null))
            }
            post {
                val post = call.receiveParameters()
                if (post["username"] != null && post["username"] == post["password"]) {
                    call.respondRedirect("/", permanent = false)
                } else {
                    call.respond(FreeMarkerContent("login.ftl", mapOf("error" to " login")))
                }
            }
/*
            authenticate("login") {
                post {
                    val principal = call.principal<UserIdPrincipal>()
                    call.respondRedirect("/", permanent = false)
                }
            }
*/
        }
    }
    /*
    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/html-freemarker") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("data" to IndexData(listOf(1, 2, 3))), ""))
        }
    }
*/
}

// data class IndexData(val items: List<Int>)

