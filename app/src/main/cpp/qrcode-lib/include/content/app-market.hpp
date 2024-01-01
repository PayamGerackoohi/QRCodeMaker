#ifndef APP_MARKET_HPP
#define APP_MARKET_HPP

#include <ostream>
#include <string>

namespace QrCodeUtil::AppMarket {

class GooglePlay {
  std::string packageName;

public:
  GooglePlay(std::string packageName);
  friend std::ostream &operator<<(std::ostream &, GooglePlay &);
  std::string str();
};

class AppleMusic {
  /**
   * Sample:
   * 	subUrl: concrete-and-gold/1249068417?mt=1&app=music
   * 	url: https://geo.itunes.apple.com/us/album/concrete-and-gold/1249068417?mt=1&app=music
   */
  std::string subUrl;

public:
  AppleMusic(std::string subUrl);
  friend std::ostream &operator<<(std::ostream &, AppleMusic &);
  std::string str();
};

class IBookStore {
  /**
   * Sample:
   * 	subUrl: crazy-rich-asians/id588213164?mt=11
   * 	url: https://geo.itunes.apple.com/us/book/crazy-rich-asians/id588213164?mt=11
   */
  std::string subUrl;

public:
  IBookStore(std::string subUrl);
  friend std::ostream &operator<<(std::ostream &, IBookStore &);
  std::string str();
};

struct AppStore {
  /**
   * Sample:
   * appId: fortnite/id1261357853?mt=8
   * link: https://itunes.apple.com/us/app/fortnite/id1261357853?mt=8
   */
  std::string appId;

public:
  AppStore(std::string appId);
  friend std::ostream &operator<<(std::ostream &, AppStore &);
  std::string str();
};

} // namespace QrCodeUtil::AppMarket

#endif
