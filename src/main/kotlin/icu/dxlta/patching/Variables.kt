package icu.dxlta.patching

import kotlin.collections.ArrayList

class Variables {

    var variables : ArrayList<String> = ArrayList();

    /**
     * gets a single variable
     */
    fun a (variable  : String) : String {
        return "window._.$variable";
    }

    fun push (variable : String) {
        variables.add("window._.$variable;")
    }

    fun get () : String {
        return "window._.dxlta={};${variables.joinToString("")};"
    }

}