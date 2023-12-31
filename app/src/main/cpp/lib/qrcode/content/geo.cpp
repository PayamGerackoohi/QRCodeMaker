#include "content/geo.hpp"
#include "util/string-util.hpp"
#include <sstream>

using std::ostream;
using std::ostringstream;
using std::string;

namespace QrCodeUtil {
Geo::Geo(int latitude, int longitude) : latitude(latitude), longitude(longitude) {}

ostream &operator<<(ostream &os, Geo &geo) {

  os << "geo:" << geo_format(geo.latitude) << "," << geo_format(geo.longitude);
  if (geo.altitude != nullptr)
    os << ',' << *geo.altitude;
  return os;
}

string Geo::str() {
  ostringstream ss;
  ss << *this;
  return ss.str();
}

} // namespace QrCodeUtil
