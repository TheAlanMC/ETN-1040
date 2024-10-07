#!/bin/bash

echo "####################"
echo "Building android APK"
echo "####################"

echo -n "Version name: "
read version_name

if [ -z "$version_name" ]; then
    version_name="1.0.0"
fi

# Package the angular application, with the name frontend
ng build --configuration android --output-path dist/frontend

# Generate the Assets:
npx capacitor-assets generate

npx cap sync android
cd android
./gradlew assembleRelease -PversionCode=1 -PversionName=$version_name
cd ..
cp android/app/build/outputs/apk/release/app-release.apk android/app/build/outputs/apk/release/app-release-$version_name.apk
cp android/app/build/outputs/apk/release/app-release.apk laboratorio_multimedia_v$version_name.apk

echo "APK built successfully!"
echo "APK file: laboratorio_multimedia_v$version_name.apk"
