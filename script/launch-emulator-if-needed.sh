#!/bin/bash
#
# Checks if there is a connected device visible to ADB. If not, then it starts the first available
# emulator.
# Expects the Android `emulator`, `tools` and `platform-tools` to be in PATH:
#
# export ANDROID_SDK=$HOME/Library/Android/sdk
# export PATH=$ANDROID_SDK/emulator:$ANDROID_SDK/tools:$ANDROID_SDK/platform-tools:$PATH
#
deviceList=$(adb devices | grep "device\$")
if [[ "$deviceList" == "" ]]; then
    avd=$(emulator -list-avds | head -n 1)
    if [[ "$avd" != "" ]]; then
        emulator -avd "$avd" -netdelay none -netspeed full &> /dev/null &
    fi
fi
