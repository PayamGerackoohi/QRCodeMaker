rm -rf .temp
mkdir .temp
curl -L \
-o .temp/qrcode.tgz \
https://github.com/PayamGerackoohi/QRCodeMaker-cpp/archive/refs/tags/v1.0.0.tar.gz

cd .temp
tar -xzf qrcode.tgz -C .

dst=../app/src/main/cpp/qrcode-lib
rm -rf $dst
mv QRCodeMaker-cpp-1.0.0/lib $dst

rm -rf .temp
