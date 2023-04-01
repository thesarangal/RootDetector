import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Class with Root Detection Functions
 *
 * @author Rajat Sarangal
 * @since April 01, 2023
 * @link https://github.com/thesarangal/RootDetector
 * */
object RootDetector {

    /**
     * @return TRUE if Any Test for Root Detection Pass out otherwise FALSE
     * */
    fun isDeviceRooted(): Boolean {
        return isSuperUserExists() || detectTestKeys() || checkSuExists() || checkBinaries() ||
                checkForDangerousProps()
    }

    /**
     * Detects if the device is using test keys. If the device is using test keys,
     * it is likely that the operating system is not a genuine version or is a custom version.
     * @return true if the device is using test keys, otherwise false.
     */
    private fun detectTestKeys(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    /**
     * Check for Root Related Binary Files
     *
     * @return TRUE if any root related binary files such as "su", "busybox", or "magisk" exist in common system directories, otherwise FALSE
     */
    private fun checkBinaries(): Boolean {
        return findBinary("su") || findBinary("busybox") || findBinary("magisk")
    }

    /**
     * Check if a root-related binary file exists in any of the commonly used root directories.
     * @param binaryName The name of the root-related binary file to search for.
     * @return true if the binary file is found in any of the common root directories, otherwise false.
     */
    private fun findBinary(binaryName: String): Boolean {
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
                return true

            }
        }
        return false
    }

    /**
     * Checks if the Superuser.apk file exists in the system directory.
     * @return true if the Superuser.apk file exists, false otherwise
     */
    private fun isSuperUserExists(): Boolean {
        val file = File("/system/app/Superuser.apk")
        return file.exists()
    }

    /**
     * This function checks whether the 'su' binary file exists in the system's path, which is an indicator of
     * the presence of root access. It uses the 'which' command to locate the 'su' binary file.
     * @return a boolean value indicating whether the 'su' binary file exists in the system's path or not.
     * If the binary exists, the function returns true; otherwise, it returns false.
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

    /**
     * Checks for the existence of potentially dangerous system properties.
     * @return true if any of the dangerous properties are found, false otherwise.
     */
    private fun checkForDangerousProps(): Boolean {
        val props = arrayOf(
            "[ro.debuggable]",
            "[ro.build.selinux]",
            "[ro.kernel.android.checkjni]",
            "[ro.kernel.android.qemud]",
            "[ro.secure]",
            "[ro.allow.mock.location]",
            "[service.adb.root]",
            "[persist.service.adb.enable]",
            "[ro.debug.buildsys.systrace]",
            "[persist.sys.root_access]",
            "[debug.firebase.analytics.app]",
            "[dalvik.vm.checkjni]"
        )
        for (prop in props) {
            val value = getProperty(prop)
            if (value.isNotEmpty()) {
                return true
            }
        }

        return false
    }

    /**
     * Returns the value of a system property.
     * @param property The name of the system property to retrieve.
     * @return The value of the system property, or an empty string if it is not set or an error occurs.
     */
    private fun getProperty(property: String): String {
        var value = ""
        try {
            val process = Runtime.getRuntime().exec("getprop $property")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            value = reader.readLine().trim()
            reader.close()
            process.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return value
    }
}
