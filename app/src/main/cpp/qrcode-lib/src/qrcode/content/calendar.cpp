#include "content/calendar.hpp"
#include <sstream>

using std::ostream;
using std::ostringstream;
using std::string;

namespace QrCodeUtil {
ostream &operator<<(ostream &os, Calendar::Event &event) {
  os << "BEGIN:VEVENT\n";

  if (event.uid != nullptr)
    os << "UID:" << *event.uid << '\n';

  if (event.creationDate != nullptr)
    os << "DTSTAMP:" << *event.creationDate << '\n';

  if (event.organizer != nullptr)
    os << "ORGANIZER;" << *event.organizer << '\n';

  if (event.start != nullptr)
    os << "DTSTART:" << *event.start << '\n';

  if (event.end != nullptr)
    os << "DTEND:" << *event.end << '\n';

  if (event.summary != nullptr)
    os << "SUMMARY:" << *event.summary << '\n';

  if (event.geo != nullptr)
    os << "GEO:" << *event.geo << '\n';

  os << "END:VEVENT";
  return os;
}

ostream &operator<<(ostream &os, Calendar &calendar) {
  os << "BEGIN:VCALENDAR\n";

  if (calendar.version != nullptr)
    os << "VERSION:" << *calendar.version << '\n';

  if (calendar.prodId != nullptr)
    os << "PRODID:" << *calendar.prodId << '\n';

  if (calendar.event != nullptr)
    os << *calendar.event << '\n';

  os << "END:VCALENDAR";
  return os;
}

string Calendar::str() {
  ostringstream ss;
  ss << *this;
  return ss.str();
}
} // namespace QrCodeUtil
