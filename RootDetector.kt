import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Class with Root Detection Functions
 *
 * @author Rajat Sarangal
 * @since January 31, 2022
 * @link https://github.com/thesarangal/RootDetector
 * */
class RootDetector {

    /**
     * @return TRUE if Any Test for Root Detection Pass out otherwise FALSE
     * */
    fun isDeviceRooted(): Boolean {
        return isSuperUserExists() ||
                detectTestKeys() ||
                checkSuExists() ||
                checkBinaries()
    }

    /**
     * Check Test Keys Available
     *
     * @return TRUE Mean No Genuine OS or Using Custom OS otherwise FALSE
     * */
    private fun detectTestKeys(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    /**
     * Check Binary Existence
     * 
     * @return TRUE if Root related binary files found otherwise FALSE
     * */
    private fun checkBinaries(): Boolean {
        return findBinary("su") || findBinary("busybox")
    }

    /**
     * Check Directories
     *
     * @param binaryName Root Related File Name
     * @return TRUE if Root related binary files found otherwise FALSE
     * */
    private fun findBinary(binaryName: String): Boolean {
        var found = false
        if (!found) {
            val places = arrayOf(
                "/sbin/",
                "/system/bin/",
                "/system/xbin/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/",
                "/data/local/bin/",
                "/data/local/xbin/",
                "/su/bin/",
                "/system/bin/.ext/",
                "/system/sd/xbin/",
                "/system/usr/we-need-root/",
                "/system/xbin/",
                "/system/app/Superuser.apk",
                "/cache",
                "/data",
                "/dev"
            )
            for (where in places) {
                if (File(where + binaryName).exists()) {
                    found = true
                    break
                }
            }
        }
        return found
    }

    /**
     * Check is SuperUser.apk Exists
     * 
     * @returnTRUE if Superuser app file found otherwise FALSE
     * */
    private fun isSuperUserExists(): Boolean {
        val file = File("/system/app/Superuser.apk")
        return file.exists()
    }

    /**
     * Checking for SU
     *
     * @return TRUE if SU exists
     */
    private fun checkSuExists(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val `in` = BufferedReader(
                InputStreamReader(process.inputStream)
            )
            `in`.readLine()
            process.destroy()
            true
        } catch (e: java.lang.Exception) {
            process?.destroy()
            false
        }
    }
}
