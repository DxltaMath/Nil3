package icu.dxlta

import java.lang.Thread.sleep
import icu.dxlta.constants.Constants as constants
import icu.dxlta.func.fetch
import icu.dxlta.func.match
import icu.dxlta.patching.Patches
import icu.dxlta.patching.Variables
import java.util.*

/** The actual DeltaMath script patcher. */
class Nil {

    /** Latest unmodified main.js */
    private var latestVanillaFile : String? = null;

    /** Latest patched main.js */
    private var latestPatchedFile : String? = null;

    /** Latest main.js URL */
    private var latestMainJsUrl : String? = null;


    /**
     * Applies patches to (unmodifiedFile).
     * @param unmodifiedFile DeltaMath's unmodified main.js file to patch
     * @author gemsvidø
     * @return The modified DeltaMath main.js file
     */
    fun patchFile (unmodifiedFile : String) : String {


        println("[patchFile] Patching main.js...")

        val variables : Variables = Variables();
        variables.push("dxlta.doNotRandomize=false")
        variables.push("dxlta.allowEscapingTimed=false")
        variables.push("dxlta.tempDnr=false")

        val patches : Patches = Patches(unmodifiedFile);

        // Allow accessing the delta.doNotRandomize variable
        patches.push("doNotRandomize=!1", "doNotRandomize=window._.dxlta.doNotRandomize")

        // Log whenever we start a timer
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

        // This hack allows you to exit running timed problems without stopping them
        patches.push("{if(\$(\".timed-start-button\").length&&\"Stop\"==\$(\".timed-start-button\").text())return alertDialog(\"You must stop the timer before pressing back. \");this.router.url.startsWith(\"/explore\")?this.router.navigate([\"/explore\"]):this.router.url.startsWith(\"/student\")?this.router.navigate([\"/student\"]):this.location.back()}", """
            {
				/* Only happens while timer is running */
				/** Allow exiting timed problems without clicking "Stop" */
				const escape = window._.dxlta.allowEscapingTimed;
				/** If the button says "Stop" */
				const stop = $(".timed-start-button").length && "Stop" == $(".timed-start-button").text();
				/* If escape is false and stop is true, then do this: */
				if (!escape && stop) return alertDialog("You must stop the timer before pressing back. ");
				/* Otherwise do this: */
				this.router.url.startsWith("/explore") ? this.router.navigate(["/explore"]) : this.router.url.startsWith("/student") ? this.router.navigate(["/student"]) : this.location.back()
			}
        """.trimIndent())

        // Before submitting a timed problem to submitTimedRecord,
        // back up doNotRandomize to tempDnr then set doNotRandomize to false
        patches.push("ProblemDataService.prototype.submitTimedRecord=function(t,e){", """
            ProblemDataService.prototype.submitTimedRecord = function(t, e) {
                if (window._.dxlta.doNotRandomize == true) {
                    window._.dxlta.doNotRandomize=false;
                    window._.dxlta.tempDnr=true;
                } else {
                    window._.dxlta.tempDnr=false;
                }
        """.trimIndent())

        // After submitting a timed problem to submitTimedRecord,
        // Restore the backup of doNotRandomize then set it to true
        patches.push("this.authHttp.post(i.requestPath,i.request).subscribe((function(t){var i=t;i.message?(alertDialog(i.message),i.hide_assignment&&(_.find(n.studentDataService.studentAssignments,(function(t){return t.id==i.hide_assignment})).hide_assignment=!0,n.router.navigate([\"/student\"]))):(n.studentDataService.updateAssignment(i.assignment),e(i.assignment))}))", """
            this.authHttp.post(i.requestPath, i.request).subscribe((function(t) {
            var i = t;
            i.message ? (alertDialog(i.message), i.hide_assignment && (_.find(n.studentDataService.studentAssignments, (function(t) {
                return t.id == i.hide_assignment
            })).hide_assignment = !0, n.router.navigate(["/student"]))) : (n.studentDataService.updateAssignment(i.assignment), e(i.assignment))
                
                if (window._.dxlta.tempDnr == true) {
                    window._.dxlta.doNotRandomize=true;
                } else if (window._.dxlta.tempDnr == false) {
                    window._.dxlta.doNotRandomize=false;
                }
                
                window._.dxlta.tempDnr=undefined;
            }))
        """.trimIndent())

        val output : String = """/* main.js - ${Date(System.currentTimeMillis()).toString()} */
            
            ${variables.get() /* Accessors */}
            
            window.oldLodash = window._;
			let lodashChecker = setInterval(() => {
				if (window.oldLodash !== window._) {
					window._ = window.oldLodash;
					clearInterval(lodashChecker);
				}
			});
            
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
    fun getMainJsUrl () : String {
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
        return latestMainJsUrl!!;
    }

    /**
     * Gets the latest non-modified main.js file. If it isn't cached, download it.
     * @author gemsvidø
     * @return DeltaMath's non-modified main.js
     */
    fun getFile () : String {
        if (latestVanillaFile === null) {
            println("[getFile] Main.js contents is not cached. Fetching it...")
            latestVanillaFile = fetch(getMainJsUrl());
        } else {
            println("[getFile] Main.js contents is cached.")
        }
        println("[getFile] Successfully obtained main.js contents.")
        return latestVanillaFile!!;
    }

    /**
     * Gets the latest patched main.js file. If it isn't cached, patch it.
     * @author gemsvidø
     * @return The latest patched main.js file
     */
    fun getPatchedFile () : String {
        if (latestPatchedFile === null) {
            println("[getPatchedFile] Patched main.js is not cached. Patching now...")
            latestPatchedFile = patchFile(getFile())
        } else {
            println("[getPatchedFile] Patched main.js is cached.")
        }
        println("[getPatchedFile] Successfully obtained patched main.js")
        return latestPatchedFile!!;
    }


    /**
     * Clears the caches every cacheInterval
     * @author gemsvidø
     */
    fun startCaching () : Unit {

        val cacheInterval : Long = 20 * 60 * 1000;



        val latestVanillaCache = Thread {
            println("[startCaching] Purging vanilla file cache every $cacheInterval milliseconds")
            while (true) {
                sleep(cacheInterval)
                latestVanillaFile = null;
            }
        }
        latestVanillaCache.start()


        val latestPatchedCache = Thread {
            println("[startCaching] Purging patched file cache every $cacheInterval milliseconds")
            while (true) {
                sleep(cacheInterval)
                latestPatchedFile = null;
            }
        }
        latestPatchedCache.start()


        val latestUrlCache = Thread {
            println("[startCaching] Purging main.js URL cache every ${cacheInterval / 2} milliseconds")
            while (true) {
                sleep(cacheInterval / 2)
                latestMainJsUrl = null;
            }
        }
        latestUrlCache.start()
    }


}