#include "content/me-card.hpp"

#include <sstream>

using std::ostream;
using std::ostringstream;
using std::string;

namespace QrCodeUtil {

MeCard::Name::Name(string firstName, string lastName) : firstName(firstName), lastName(lastName) {}

ostream &operator<<(ostream &os, MeCard::Name &name) {
  os << "N:" << name.lastName << "," << name.firstName;
  return os;
}

ostream &operator<<(ostream &os, MeCard &card) {
  os << "MECARD:";

  if (card.name != nullptr)
    os << *card.name << ';';

  if (card.phone != nullptr)
    os << "TEL:" << *card.phone << ';';

  if (card.videoPhone != nullptr)
    os << "TEL-AV:" << *card.videoPhone << ';';

  if (card.email != nullptr)
    os << "EMAIL:" << *card.email << ';';

  if (card.address != nullptr)
    os << "ADR:" << *card.address << ';';

  if (card.birthDay != nullptr)
    os << "BDAY:" << *card.birthDay << ';';

  if (card.nickName != nullptr)
    os << "NICKNAME:" << *card.nickName << ';';

  if (card.note != nullptr)
    os << "NOTE:" << *card.note << ';';

  if (card.url != nullptr)
    os << "URL:" << *card.url << ';';

  os << ";";
  return os;
}

string MeCard::str() {
  ostringstream ss;
  ss << *this;
  return ss.str();
}

} // namespace QrCodeUtil
