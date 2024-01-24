# Run the script on the project root directory

# Break on error
set -e

# Validate the 'adb'
adb=`cat scripts/env | grep adb | cut -d = -f 2`
${adb} --version | grep "Android Debug Bridge version" > /dev/null || (echo "Invalid adb" && exit 1)

# Validate the 'JAVA_HOME'
JAVA_HOME=`cat scripts/env | grep JAVA_HOME | cut -d = -f 2`
pushd "${JAVA_HOME}/bin" > /dev/null
ls | grep jar > /dev/null || (echo "Invalid JAVA_HOME: jar not found" && exit 1)
ls | grep java > /dev/null || (echo "Invalid JAVA_HOME: java not found" && exit 1)
popd > /dev/null

# Clean the target and the device
target="sdcard/Documents/QrCodeMaker/screenshots"
rm -rf docs/screenshots
${adb} shell rm -rf ${target}

# Run the instrumented tests
./gradlew connectedDebugAndroidTest

# Move the screenshots to the target
pushd docs/ > /dev/null
${adb} pull ${target}
popd > /dev/null
