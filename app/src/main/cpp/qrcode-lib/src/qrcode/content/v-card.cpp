#include "content/v-card.hpp"
#include "util/string-util.hpp"
#include <sstream>

using std::ostream;
using std::ostringstream;
using std::string;

namespace QrCodeUtil {
VCard::Name::Name(string prefix, string firstName, string middleName, string lastName,
                  string suffix)
    : prefix(prefix), firstName(firstName), middleName(middleName), lastName(lastName),
      suffix(suffix) {}

ostream &operator<<(ostream &os, VCard::Name &name) {
  name << os;
  return os;
}

ostream &VCard::Name::operator<<(ostream &os) {
  os << "N:" << lastName << ";" << firstName << ";" << middleName << ";" << prefix << ";" << suffix;
  return os;
}

VCard::Phone::Phone(string type, string number) : type(type), number(number) {}

ostream &operator<<(ostream &os, VCard::Phone &phone) {
  os << "TEL;"
     << "TYPE=" << phone.type << ":" << phone.number;
  return os;
}

VCard::Address::Address(string type, string value) : type(type), value(value) {}

ostream &operator<<(ostream &os, VCard::Address &address) {
  os << "ADR;TYPE=" << address.type << ":" << address.value;
  return os;
}

ostream &operator<<(ostream &os, VCard &vCard) {
  os << "BEGIN:VCARD\n";
  os << "VERSION:4.0\n";

  if (vCard.name != nullptr)
    os << *vCard.name << '\n';

  if (vCard.phone != nullptr)
    os << *vCard.phone << '\n';

  if (vCard.address != nullptr)
    os << *vCard.address << '\n';

  if (vCard.agent != nullptr)
    os << "AGENT:" << *vCard.agent << '\n';

  if (vCard.anniversary != nullptr)
    os << "ANNIVERSARY:" << *vCard.anniversary << '\n';

  if (vCard.birthDay != nullptr)
    os << "BDAY:" << *vCard.birthDay << '\n';

  if (vCard.calAdrUri != nullptr)
    os << "CALADRURI:" << *vCard.calAdrUri << '\n';

  if (vCard.calUri != nullptr)
    os << "CALURI:" << *vCard.calUri << '\n';

  if (vCard.categories.size() != 0)
    os << "CATEGORIES:" << join(vCard.categories);

  if (vCard.clazz != nullptr)
    os << "CLASS:" << *vCard.clazz << '\n';

  if (vCard.clientPidMap != nullptr)
    os << "CLIENTPIDMAP:" << *vCard.clientPidMap << '\n';

  if (vCard.email != nullptr)
    os << "EMAIL:" << *vCard.email << '\n';

  if (vCard.fbUrl != nullptr)
    os << "FBURL:" << *vCard.fbUrl << '\n';

  if (vCard.formattedName != nullptr)
    os << "FB:" << *vCard.formattedName << '\n';

  if (vCard.gender != nullptr)
    os << "GENDER:" << *vCard.gender << '\n';

  if (vCard.geo != nullptr)
    os << "GEO:geo:" << *vCard.geo << '\n';

  if (vCard.impp != nullptr)
    os << "IMPP:" << *vCard.impp << '\n';

  if (vCard.key != nullptr)
    os << "KEY;" << *vCard.key << '\n';

  if (vCard.kind != nullptr)
    os << "KIND:" << *vCard.kind << '\n';

  if (vCard.label != nullptr)
    os << "LABEL;" << *vCard.label << '\n';

  if (vCard.lang != nullptr)
    os << "LANG:" << *vCard.lang << '\n';

  if (vCard.logo != nullptr)
    os << "LOGO;" << *vCard.logo << '\n';

  if (vCard.mailer != nullptr)
    os << "MAILER:" << *vCard.mailer << '\n';

  if (vCard.member != nullptr)
    os << "MEMBER:" << *vCard.member << '\n';

  if (vCard.nicknames.size() != 0)
    os << "NICKNAME:" << join(vCard.nicknames) << '\n';

  if (vCard.note != nullptr)
    os << "NOTE:" << *vCard.note << '\n';

  if (vCard.org != nullptr)
    os << "ORG:" << *vCard.org << '\n';

  if (vCard.photo != nullptr)
    os << "PHOTO;" << *vCard.photo << '\n';

  if (vCard.prodId != nullptr)
    os << "PRODID:" << *vCard.prodId << '\n';

  if (vCard.profile != nullptr)
    os << "PROFILE:" << *vCard.profile << '\n';

  if (vCard.related != nullptr)
    os << "RELATED;" << *vCard.related << '\n';

  if (vCard.rev != nullptr)
    os << "REV:" << *vCard.rev << '\n';

  if (vCard.role != nullptr)
    os << "ROLE:" << *vCard.role << '\n';

  if (vCard.sortString != nullptr)
    os << "SORT-STRING:" << *vCard.sortString << '\n';

  if (vCard.sound != nullptr)
    os << "SOUND;" << *vCard.sound << '\n';

  if (vCard.source != nullptr)
    os << "SOURCE:" << *vCard.source << '\n';

  if (vCard.title != nullptr)
    os << "TITLE:" << *vCard.title << '\n';

  if (vCard.timeZone != nullptr)
    os << "TZ:" << *vCard.timeZone << '\n';

  if (vCard.uid != nullptr)
    os << "UID:" << *vCard.uid << '\n';

  if (vCard.url != nullptr)
    os << "URL:" << *vCard.url << '\n';

  if (vCard.xml != nullptr)
    os << "XML:" << *vCard.xml << '\n';

  os << "END:VCARD";
  return os;
}

string VCard::str() {
  ostringstream ss;
  ss << *this;
  return ss.str();
}
} // namespace QrCodeUtil
