![GitHub Release](https://img.shields.io/github/v/release/PayamGerackoohi/QRCodeMaker?style=plastic)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/PayamGerackoohi/QRCodeMaker/main-cicd.yml?style=plastic)
![Coverage](https://img.shields.io/badge/coverage-100%25-brightgreen?style=plastic)
![GitHub top language](https://img.shields.io/github/languages/top/PayamGerackoohi/QRCodeMaker?style=plastic)
![GitHub last commit](https://img.shields.io/github/last-commit/PayamGerackoohi/QRCodeMaker?style=plastic)
![GitHub License](https://img.shields.io/github/license/PayamGerackoohi/QRCodeMaker?style=plastic)

![GitHub repo file or directory count](https://img.shields.io/github/directory-file-count/PayamGerackoohi/QRCodeMaker?style=plastic)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/PayamGerackoohi/QRCodeMaker?style=plastic)
![GitHub repo size](https://img.shields.io/github/repo-size/PayamGerackoohi/QRCodeMaker?style=plastic)

# QR-Code Maker Android App
It makes QR-Code images based on casual user contents, targeted for cell-phone usecases.

## Supported Content Types (Currently)
- Raw Text
- Phone Call
- Me-Card

# Screenshots
The screenshots are automatically captured, resized and compressed from the instrumented testes.

## Splashscreen
![word detail](docs/screenshots/Splashscreen.webp)

## Home
![word detail](docs/screenshots/Home.webp)

## Content Type
![word detail](docs/screenshots/ContentType.webp)

## Content Form
### Insert Mode
![word detail](docs/screenshots/ContentForm_InsertMode_Text.webp)
![word detail](docs/screenshots/ContentForm_InsertMode_PhoneCall.webp)
![word detail](docs/screenshots/ContentForm_InsertMode_MeCard.webp)

### Edit Mode
![word detail](docs/screenshots/ContentForm_EditMode.webp)

## Show QR-Code
### Portrait
![word detail](docs/screenshots/ShowQrCode_Portrait.webp)
![word detail](docs/screenshots/ShowQrCode_Portrait_Fullscreen.webp)
![word detail](docs/screenshots/ShowQrCode_Toolbox.webp)
![word detail](docs/screenshots/ShowQrCode_Toolbox_RemoveContent.webp)

### Landscape
![word detail](docs/screenshots/ShowQrCode_Landscape.webp)
![word detail](docs/screenshots/ShowQrCode_Landscape_Fullscreen.webp)

# Code Quality
## Test Results ✅
### Unit Tests ![Unit Tests](https://img.shields.io/badge/passed-100%25-brightgreen?style=plastic)
![word detail](docs/test-results/unit_tests.webp)
### UI Tests ![UI Tests](https://img.shields.io/badge/passed-100%25-brightgreen?style=plastic)
![word detail](docs/test-results/ui_tests.webp)

## Test Coverage 👍
### Kover Report ![Kover Report](https://img.shields.io/badge/coverage-100%25-brightgreen?style=plastic)
![word detail](docs/test-results/kover.webp)
### Jacoco Report ![Jacoco Report](https://img.shields.io/badge/coverage-98%25-green?style=plastic)
![word detail](docs/test-results/jacoco.webp)

# Scripts
Some Unix scripts to make the external source management and screenshot capturing automatic.
Run the scripts from the project root directory.

## `download-install-qrcode-cpp.sh`
It downloads the [QRCodeMaker-cpp](https://github.com/PayamGerackoohi/QRCodeMaker-cpp/) github project and installs it on the `./app/src/main/cpp/qrcode-lib` directory. It provides the core functionality of the app.

```sh
./scripts/download-install-qrcode-cpp.sh
```

## `take-screenshots.sh`
<!-- - Edit `adb` and `JAVA_HOME` in the script to the  -->
For the first time, set your `adb` and `JAVA_HOME` for `jdk-17` into the `env` file. Similar this on OSX:
```sh
rm -f scripts/env
echo 'adb=/Users/payam1991gr/Library/Android/sdk/platform-tools/adb' >> scripts/env
echo 'JAVA_HOME=/Applications/Android Studio.app/Contents/jbr/Contents/Home' >> scripts/env
```

From now on
- Run an android emulator device 
- Run the script
```sh
./scripts/take-screenshots.sh
```
The results are stored in the `./docs/screenshots` directory.

❌ Don't put anything inside the `./docs/screenshots` folder. It would be cleaned-up everytime you call the `take-screenshots.sh` script.

# TODO
- [ ] Placing image inside the QR-Code
- [ ] More content types
