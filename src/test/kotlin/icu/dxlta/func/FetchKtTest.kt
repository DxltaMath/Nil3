package icu.dxlta.func

import io.javalin.Javalin
import org.junit.jupiter.api.Test
import java.net.URI
import java.net.URL

internal class FetchKtTest {

    @Test
    fun testFetchURL () {

        val app = Javalin.create { config ->
            config.http.defaultContentType = "text/plain";
        }.start(1115)


        app.get("/") { ctx ->
            ctx.result("Hello World!")
        }

        app.get("/empty") { ctx ->
            ctx.result("")
        }

        val target : URL = URL("http://localhost:${app.port()}")

        println("HELLO WORLD: ${fetch(target)}/")
        println("EMPTY: ${fetch(target)}/empty")

        app.stop();

    }

    @Test
    fun testFetchString () {
        val app = Javalin.create { config ->
            config.http.defaultContentType = "text/plain";
        }.start(1116)


        app.get("/") { ctx ->
            ctx.result("Hello World!")
        }

        app.get("/empty") { ctx ->
            ctx.result("")
        }

        val target : String = "http://localhost:${app.port()}"

        println("HELLO WORLD: ${fetch(target)}/")
        println("EMPTY: ${fetch(target)}/empty")

        app.stop();
    }

    @Test
    fun testFetchURI () {

        val app = Javalin.create { config ->
            config.http.defaultContentType = "text/plain";
        }.start(1117)


        app.get("/") { ctx ->
            ctx.result("Hello World!")
        }

        app.get("/empty") { ctx ->
            ctx.result("")
        }

        val target : URI = URI("http://localhost:${app.port()}")

        println("HELLO WORLD: ${fetch(target)}/")
        println("EMPTY: ${fetch(target)}/empty")

        app.stop()
    }
}