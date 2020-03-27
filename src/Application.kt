package com.example

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import freemarker.cache.*
import io.ktor.features.PartialContent
import io.ktor.freemarker.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(PartialContent) {

    }
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    routing() {
        get("/login") {
            call.respond(FreeMarkerContent("login.ftl", null))
        }
        post("/login") {
            val post = call.receiveParameters()
            if (post["username"] != null && post["username"] == post["password"]) {
                call.respondText("OK")
            } else {
                call.respond(FreeMarkerContent("login.ftl", mapOf("error" to "Invalid login")))
            }
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

data class IndexData(val items: List<Int>)

