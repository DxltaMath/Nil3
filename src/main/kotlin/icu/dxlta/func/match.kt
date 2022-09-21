package icu.dxlta.func

/** Captures a String from a regex */
fun String.match (regex : String) : String {
    return this.match(Regex(regex))
}

/** Captures a String from a regex */
fun String.match (regex : Regex) : String {
    val x : String? = regex.find(this)?.value
    return if (x !== null) x else ""
}