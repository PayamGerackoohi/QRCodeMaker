#include "content/biz-card.hpp"

#include <sstream>

using std::ostringstream;
using std::string;

namespace QrCodeUtil {
std::ostream &operator<<(std::ostream &os, BizCard &card) {
  os << "BIZCARD:";

  if (card.firstName != nullptr)
    os << "N:" << *card.firstName << ";";

  if (card.lastName != nullptr)
    os << "X:" << *card.lastName << ";";

  if (card.title != nullptr)
    os << "T:" << *card.title << ";";

  if (card.company != nullptr)
    os << "C:" << *card.company << ";";

  if (card.address != nullptr)
    os << "A:" << *card.address << ";";

  if (card.phone != nullptr)
    os << "B:" << *card.phone << ";";

  if (card.email != nullptr)
    os << "E:" << *card.email << ";";

  os << ";";
  return os;
}

string BizCard::str() {
  ostringstream ss;
  ss << *this;
  return ss.str();
}
} // namespace QrCodeUtil
