package icu.dxlta

import icu.dxlta.func.fetch
import io.javalin.Javalin
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MainKtTest {

    @Test
    fun main () {

        val patcher : Nil = Nil()

        val app = Javalin.create { config ->
            config.http.defaultContentType = "text/javascript";
        }.start(1111)

        app.get("/app/main.js") { ctx ->
            ctx.result(patcher.getPatchedFile())
        }

        assertEquals(patcher.getPatchedFile(), fetch("http://localhost:${app.port()}/app/main.js"))

        app.stop();
    }
}