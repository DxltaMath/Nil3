package icu.dxlta.patching

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import javax.script.ScriptEngine
import javax.script.ScriptEngineFactory
import javax.script.ScriptEngineManager

internal class PatchesTest {

    @Test
    fun testPatches () {

        val input : String = "(function(x){console.log(x);})('not success')"
        val expect : String = "(function(x){console.log('success'); return 'ignore ' + x;})('not success')"

        val patches : Patches = Patches(input)
        patches.push("{console.log(x);}", "{console.log('success'); return 'ignore ' + x;}")

        val output : String = patches.get()

        println("Input: $input \nExpected: $expect \nActual: $output")

        assertEquals(expect, output)
    }
}