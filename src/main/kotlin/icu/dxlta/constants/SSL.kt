package icu.dxlta.constants

/** HTTPS/SSL configuration */
object SSL {

    /** Set this to true if you are not using a proxy, and need to use HTTPS included in Nil.
     * CAUTION: Remember to set HTTPS_KEY_PATH and HTTPS_CHAIN_PATH to your SSL certificate. */
    const val USE_HTTPS : Boolean = false

    /** Insert your own path to the privatekey.pem SSL certificate here. If not, Nil's default one will be used.
     * CAUTION: Remember to use the full path, and change this to YOUR DOMAIN's SSL Certificate. DO NOT LEAK THIS FILE. */
    const val HTTPS_KEY_PATH : String = "/etc/letsencrypt/live/nil.dxlta.icu/privkey.pem"

    /** Insert your own path to the fullchain.pem SSL certificate here. If not, Nil's default one will be used.
     * CAUTION: Remember to use the full path, and change this to YOUR DOMAIN's SSL Certificate. */
    const val HTTPS_CHAIN_PATH : String = "/etc/letsencrypt/live/nil.dxlta.icu/fullchain.pem"

    /** Override, if not it'll default to 443.
     * CAUTION: Remember that server port 443 is the default http port, and port 443 is the default HTTPS port. */
    const val HTTPS_PORT : Int = 443
}