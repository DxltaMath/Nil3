package icu.dxlta.patching

/** Replacements  */
class Patches (original : String) {

    private val content : String;

    private var replacements : HashMap<String, String> = HashMap();


    init {
        content = original;
    }

    /** Replace occurrences of one string with another. */
    fun push (replace: String, set: String) : Unit {
        replacements.put(replace, set)
    }

    fun get () : String {

        var out : String = content;

        replacements.forEach { entry ->
            out = out.replace(entry.key, entry.value)
        }

        return out;
    }
}