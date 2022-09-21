package icu.dxlta.constants

import java.io.FileReader

/** Website constants */
object Website {

    /** Insert your own contents of index.html here, if not Nil's normal index.html will be used.
     * CAUTION: Remember that this is the exported main page of the site. Be wise. */
    val INDEX : String = FileReader("./public/index.html").readText()

    /** Insert your own contents of style.css here, if not Nil's normal style.css will be used.
     * CAUTION: This is exported at `<site>/style.css`. Remember to insert a stylesheet link in index.html to use this. */
    val STYLE : String = FileReader("./public/style.css").readText()
}