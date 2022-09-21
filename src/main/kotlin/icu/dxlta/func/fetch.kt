package icu.dxlta.func

import java.net.URI
import java.net.URL
import java.util.*
import kotlin.NoSuchElementException

/** Get text from an online resource */
fun fetch (url : URL) : String {
    println("Fetching resource from $url")
    return try {
        Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next();
    } catch (ex : NoSuchElementException) {
        "";
    }
}

fun fetch (url : String) : String {
    return fetch(URL(url))
}

fun fetch (url : URI) : String {
    return fetch(url.toURL())
}
