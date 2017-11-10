cd android
call gradlew clean
call gradlew assembleRelease
adb install -r app\build\outputs\apk\app-release.apk
adb shell am start -n com.kiekkohamsteri/com.kiekkohamsteri.MainActivity
cd ..
