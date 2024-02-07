set -e

rm -rf keystore
mkdir keystore
gpg --quiet --batch --yes --decrypt --passphrase="$GPG_KEYSTORE_PASSPHRASE" \
--output keystore/keystore.tgz gpg/PayamGr-keystore.gpg

pushd keystore > /dev/null
tar -xzf keystore.tgz
rm keystore.tgz
popd > /dev/null
