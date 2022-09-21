package icu.dxlta.constants

import icu.dxlta.func.fetch
import io.javalin.Javalin
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*


internal class WebsiteTest {

    @Test fun index () {

        val app = Javalin.create { config ->
            config.http.defaultContentType = "text/html";
        }.start(1112)

        app.get("/") { ctx ->
            ctx.result(Website.INDEX)
        }

        assertEquals(Website.INDEX, fetch("http://localhost:${app.port()}/"))

        app.stop();
    }

    @Test fun indexRedirect () {

        val app = Javalin.create { config ->
            config.http.defaultContentType = "text/html";
        }.start(1113)

        app.get("/") { ctx ->
            ctx.result(Website.INDEX)
        }

        app.get("/index.html") { ctx ->
            ctx.redirect("/")
        }

        assertEquals(Website.INDEX, fetch("http://localhost:${app.port()}/index.html"))

        app.stop();
    }

    @Test fun style () {

        val app = Javalin.create { config ->
            config.http.defaultContentType = "text/css";
        }.start(1114)

        app.get("/style.css") { ctx ->
            ctx.result(Website.STYLE)
        }

        assertEquals(Website.STYLE, fetch("http://localhost:${app.port()}/style.css"))

        app.stop();
    }

}