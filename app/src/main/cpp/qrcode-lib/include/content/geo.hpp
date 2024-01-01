#ifndef GEO_HPP
#define GEO_HPP

#include <memory>
#include <ostream>
#include <string>

namespace QrCodeUtil {
class Geo {
  /**
   * 100'000 * latitude in degrees
   * Sample: 4'071'872 means 40.71872 deg N
   */
  int latitude;
  /**
   * 100'000 * longitude in degrees
   * Sample: -7'398'905 means 73.98905 deg W
   */
  int longitude;

public:
  /**
   * altitude in meters
   */
  std::unique_ptr<int> altitude;

  Geo(int latitude, int longitude);
  friend std::ostream &operator<<(std::ostream &, Geo &);
  std::string str();
};
} // namespace QrCodeUtil

#endif
