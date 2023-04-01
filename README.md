# Root Detector

Root Detector is a Kotlin-based library that detects whether an Android device has been rooted or
not. The library checks for various root indicators such as the presence of root-related binary
files, system properties, and superuser.apk file, etc.

## Usage

To use the Root Detector library, simply call the isDeviceRooted() method provided by the
RootDetector object. The method returns a boolean value indicating whether the device is rooted or
not. Here is an example of how to use the library:

```bash
if (RootDetector.isDeviceRooted()) {
    // Device is rooted
} else {
    // Device is not rooted
}
```

## Detection Techniques

The Root Detector library uses the following techniques to detect root access on an Android device:

- Checking for the presence of root-related binary files such as su, busybox, and magisk in common
  system directories.
- Checking for the presence of the Superuser.apk file in the system directory.
- Checking for the existence of potentially dangerous system properties.
- Using the which command to locate the su binary file in the system's path.
- Detecting whether the device is using test keys or not.

## Contributions

Contributions to the Root Detector library are welcome. If you find a bug or have a feature request,
please open an issue on the [GitHub repository](https://github.com/thesarangal/RootDetector).

## License

The Root Detector library is released under the [MIT License](https://opensource.org/licenses/MIT).
