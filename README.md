# PinkStation 4 VR

Experimental Android/Meta Quest starter project. The current build is a UI and native ARM64 loading proof-of-concept; it is **not a PS4 emulator** yet.

## Build an APK on GitHub

1. Open the **Actions** tab.
2. Select **Build PinkStation 4 VR APK**.
3. Press **Run workflow** on `main` (pushes to `main` also start builds).
4. Wait for the run to finish successfully.
5. Open the run and download the `PinkStation4VR-debug-apk` artifact.

The output is an unsigned debug APK for development/testing on compatible Android devices. This initial target uses `arm64-v8a` and Android API 29+.

## Current scope

This first milestone proves the Android project and native ARM64 library can be packaged. PlayStation 4 emulation, graphics translation, CPU emulation, system software support, and VR controls are not implemented.
