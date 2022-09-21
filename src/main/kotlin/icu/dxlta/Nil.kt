package icu.dxlta

import java.lang.Thread.sleep
import icu.dxlta.constants.Constants as constants
import icu.dxlta.func.fetch
import icu.dxlta.func.match
import icu.dxlta.patching.Patches
import icu.dxlta.patching.Variables
import java.util.*

/** The actual DeltaMath script patcher. */
object Nil {

    /** Latest unmodified main.js */
    @JvmStatic private var latestVanillaFile : String? = null;

    /** Latest patched main.js */
    @JvmStatic private var latestPatchedFile : String? = null;

    /** Latest main.js URL */
    @JvmStatic private var latestMainJsUrl : String? = null;


    /**
     * Applies patches to (unmodifiedFile).
     * @param unmodifiedFile DeltaMath's unmodified main.js file to patch
     * @author gemsvidø
     * @return The modified DeltaMath main.js file
     */
    @JvmStatic fun patchFile (unmodifiedFile : String) : String {

        println("[patchFile] Patching main.js...")

        val variables : Variables = Variables();
        variables.push("delta.doNotRandomize=false")
        variables.push("delta.allowEscapingTimed=false")

        val patches : Patches = Patches(unmodifiedFile);
        patches.push("doNotRandomize=!1", "doNotRandomize=window.delta.doNotRandomize")
        patches.push("function y(t){return function(e){if(\"__ngUnwrap__\"===e)return t;!1===t(e)&&(e.preventDefault(),e.returnValue=!1)}}", """
            function y(t) {
                return function(e) {
                    if (e.path[0].tagName === "BUTTON" && e.path[0].className === "btn btn-default timed-start-button") {
                        console.log("Timer toggle (prevent OFF)");
                    }
                    if ("__ngUnwrap__" === e) return t;
                    !1 === t(e) && (e.preventDefault(), e.returnValue = !1)
                }
            }
        """.trimIndent())
        patches.push("{if(\$(\".timed-start-button\").length&&\"Stop\"==\$(\".timed-start-button\").text())return alertDialog(\"You must stop the timer before pressing back. \");this.router.url.startsWith(\"/explore\")?this.router.navigate([\"/explore\"]):this.router.url.startsWith(\"/student\")?this.router.navigate([\"/student\"]):this.location.back()}", """
            {
				/* Only happens while timer is running */
				/** Allow exiting timed problems without clicking "Stop" */
				const escape = window.delta.allowEscapingTimed;
				/** If the button says "Stop" */
				const stop = ${'$'}(".timed-start-button").length && "Stop" == ${'$'}(".timed-start-button").text();
				/* If escape is false and stop is true, then do this: */
				if (!escape && stop) return alertDialog("You must stop the timer before pressing back. ");
				/* Otherwise do this: */
				this.router.url.startsWith("/explore") ? this.router.navigate(["/explore"]) : this.router.url.startsWith("/student") ? this.router.navigate(["/student"]) : this.location.back()
			}
        """.trimIndent())

        val output : String = """/* main.js - ${Date(System.currentTimeMillis()).toString()} */
            
            ${variables.get() /* Accessors */}
            
            ${patches.get() /* Patched main.js */}
            
            
            console.log("%cNil", "font-size:69px;color:#540052;font-weight:900;font-family:sans-serif;");
			console.log("%cVersion ${constants.VERSION}", "font-size:20px;color:#000025;font-weight:700;font-family:sans-serif;");
			
			/* Load the Delta Math Cheat GUI */
			(async () => {
				await new Promise(r => setTimeout(r, 5000));
				await eval(await (await fetch("${constants.GUI_LINK}")).text());
			})();
			console.trace = () => {};
        """.trimIndent();

        println("[patchFile] Successfully patched main.js")
        return output;
    }


    /**
     * Gets the latest URL to the main.js
     * @author gemsvidø
     * @return The URL to DeltaMath's main.js file
     */
    @JvmStatic fun getMainJsUrl () : String {
        if (latestMainJsUrl === null) {
            println("[getMainJsUrl] Main.js url is not cached. Fetching one...")
            val html : String = fetch("https://www.deltamath.com/app/")
            val output : String = "https://www.deltamath.com/app/" + html.match("""main\..{0,40}\.js""")
            latestMainJsUrl = output;
            println("[getMainJsUrl] Fetched main.js url.")
        } else {
            println("[getMainJsUrl] Main.js url is cached.")
        }
        println("[getMainJsUrl] Successfully obtained main.js url")
        return latestMainJsUrl as String;
    }

    /**
     * Gets the latest non-modified main.js file. If it isn't cached, download it.
     * @author gemsvidø
     * @return DeltaMath's non-modified main.js
     */
    @JvmStatic fun getFile () : String {
        if (latestVanillaFile === null) {
            println("[getFile] Main.js contents is not cached. Fetching it...")
            latestVanillaFile = fetch(getMainJsUrl());
        } else {
            println("[getFile] Main.js contents is cached.")
        }
        println("[getFile] Successfully obtained main.js contents.")
        return latestVanillaFile as String;
    }

    /**
     * Gets the latest patched main.js file. If it isn't cached, patch it.
     * @author gemsvidø
     * @return The latest patched main.js file
     */
    @JvmStatic fun getPatchedFile () : String {
        if (latestPatchedFile === null) {
            println("[getPatchedFile] Patched main.js is not cached. Patching now...")
            latestPatchedFile = patchFile(getFile())
        } else {
            println("[getPatchedFile] Patched main.js is cached.")
        }
        println("[getPatchedFile] Successfully obtained patched main.js")
        return latestPatchedFile as String;
    }


    /**
     * Clears the caches every cacheInterval
     * @author gemsvidø
     */
    @JvmStatic fun startCaching (args: Args) : Unit {

        // Preserve cache
        if (args.cacheInterval.toInt() == -1) {
            println("[startCaching] Cache is in preserve mode and will not be reset.")
            return;
        } else if (args.cacheInterval < 1000) { // Reset cache every 1 second if no caching
            println("[startCaching] Cache was set to purge faster than every second. Setting to every second.")
            args.cacheInterval = 1000
        }

        val latestVanillaCache = Thread {
            println("[startCaching] Purging vanilla file cache every ${args.cacheInterval} milliseconds")
            while (true) {
                sleep(args.cacheInterval)
                latestVanillaFile = null;
            }
        }
        latestVanillaCache.start()


        val latestPatchedCache = Thread {
            println("[startCaching] Purging patched file cache every ${args.cacheInterval} milliseconds")
            while (true) {
                sleep(args.cacheInterval)
                latestPatchedFile = null;
            }
        }
        latestPatchedCache.start()


        val latestUrlCache = Thread {
            println("[startCaching] Purging main.js URL cache every ${args.cacheInterval / 2} milliseconds")
            while (true) {
                sleep(args.cacheInterval / 2)
                latestMainJsUrl = null;
            }
        }
        latestUrlCache.start()
    }


}