package icu.dxlta

import java.lang.Integer.parseInt

class Args(args: Array<String>) {

    var cacheInterval : Long = 20 * 60 * 1000;


    init {

        if (args.contains("-nc") || args.contains("--no-cache")) {
            cacheInterval = 1000;
        } else if (args.contains("-c")) {
            cacheInterval = parseInt(args[args.indexOf("-c") + 1]).toLong();
        } else if (args.contains("--cache-interval")) {
            cacheInterval = parseInt(args[args.indexOf("--cache-interval") + 1]).toLong();
        } else if (args.contains("-nu") || args.contains("--no-update") || args.contains("--never-update")) {
            cacheInterval = -1
        }

        println("Caching interval: $cacheInterval")


    }

}