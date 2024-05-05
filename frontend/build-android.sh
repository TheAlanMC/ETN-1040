#!/bin/bash

echo "####################"
echo "Building android APK"
echo "####################"

echo -n "Version name: "
read version_name

if [ -z "$version_name" ]; then
    version_name="1.0.0"
fi

npx cap sync android
cd android
./gradlew assembleRelease -PversionCode=1 -PversionName=$version_name
cd ..
cp android/app/build/outputs/apk/release/app-release.apk laboratorio_multimedia.apk

echo "APK built successfully!"
echo "APK file: laboratorio_multimedia.apk"
