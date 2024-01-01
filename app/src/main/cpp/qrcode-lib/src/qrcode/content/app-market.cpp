#include "content/app-market.hpp"
#include <sstream>

using std::ostream;
using std::ostringstream;
using std::string;

namespace QrCodeUtil::AppMarket {

GooglePlay::GooglePlay(string packageName) : packageName(packageName) {}

ostream &operator<<(ostream &os, GooglePlay &GooglePlay) {
  os << "market://details?id=" << GooglePlay.packageName;
  return os;
}

string GooglePlay::str() {
  ostringstream ss;
  ss << *this;
  return ss.str();
}

AppleMusic::AppleMusic(string subUrl) : subUrl(subUrl) {}

ostream &operator<<(ostream &os, AppleMusic &appleMusic) {
  os << "https://geo.itunes.apple.com/us/album/" << appleMusic.subUrl;
  return os;
}

string AppleMusic::str() {
  ostringstream ss;
  ss << *this;
  return ss.str();
}

IBookStore::IBookStore(string subUrl) : subUrl(subUrl) {}

ostream &operator<<(ostream &os, IBookStore &iBookStore) {
  os << "https://geo.itunes.apple.com/us/book/" << iBookStore.subUrl;
  return os;
}

string IBookStore::str() {
  ostringstream ss;
  ss << *this;
  return ss.str();
}

AppStore::AppStore(string appId) : appId(appId) {}

ostream &operator<<(ostream &os, AppStore &appStore) {
  os << "https://itunes.apple.com/us/app/" << appStore.appId;
  return os;
}

string AppStore::str() {
  ostringstream ss;
  ss << *this;
  return ss.str();
}

} // namespace QrCodeUtil::AppMarket
