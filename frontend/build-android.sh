#!/bin/bash

echo "####################"
echo "Building android APK"
echo "####################"

echo -n "Version name: "
read version_name

if [ -z "$version_name" ]; then
    version_name="1.0.0"
fi

# Package the Angular App:
ng build --configuration android --output-path dist/frontend

# Generate the Assets:
npx capacitor-assets generate

npx cap sync android
cd android
# Generate the APK file
./gradlew assembleRelease -PversionCode=1 -PversionName=$version_name
# Generate the AAB file
./gradlew bundleRelease -PversionCode=1 -PversionName=$version_name
cd ..
cp android/app/build/outputs/bundle/release/app-release.aab laboratorio_multimedia_v$version_name.aab
cp android/app/build/outputs/apk/release/app-release.apk laboratorio_multimedia_v$version_name.apk

echo "APK and AAB files generated successfully"
echo "APK: laboratorio_multimedia_v$version_name.apk"
echo "AAB: laboratorio_multimedia_v$version_name.aab"
