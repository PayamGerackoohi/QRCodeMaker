# Run the script on the project root directory

# Break on error
set -e

# Download the library
rm -rf .temp
mkdir .temp
curl -L \
-o .temp/qrcode.tgz \
https://github.com/PayamGerackoohi/QRCodeMaker-cpp/archive/refs/tags/v1.0.0.tar.gz

# Decompress the archive
cd .temp
tar -xzf qrcode.tgz -C .

# Install the library
dst=../app/src/main/cpp/qrcode-lib
rm -rf $dst
mv QRCodeMaker-cpp-1.0.0/lib $dst

# Cleanup
rm -rf .temp
