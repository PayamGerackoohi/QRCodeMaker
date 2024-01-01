#ifndef VCARD_HPP
#define VCARD_HPP

#include <memory>
#include <ostream>
#include <string>
#include <vector>

namespace QrCodeUtil {

struct VCard {
  class Name {
    std::string prefix;
    std::string firstName;
    std::string middleName;
    std::string lastName;
    std::string suffix;

  public:
    Name(std::string prefix, std::string firstName, std::string middleName, std::string lastName,
         std::string suffix);
    std::ostream &operator<<(std::ostream &);
    friend std::ostream &operator<<(std::ostream &, Name &);
  };

  class Phone {
    /** type: Mobile, cell, ... */
    std::string type;
    std::string number;

  public:
    Phone(std::string type, std::string number);
    friend std::ostream &operator<<(std::ostream &, Phone &);
  };

  class Address {
    std::string type;
    std::string value;

  public:
    Address(std::string type, std::string value);
    friend std::ostream &operator<<(std::ostream &, Address &);
  };

  // ### <MOBILE> ###
  std::unique_ptr<Name> name;
  std::unique_ptr<Phone> phone;
  std::vector<std::string> nicknames;
  /**
   * Specifies the job title, functional position or function of the individual associated with the
   * vCard object within an organization.
   */
  std::unique_ptr<std::string> title;
  /**
   * The name and optionally the unit(s) of the organization associated with the vCard object. This
   * property is based on the X.520 Organization Name attribute and the X.520 Organization Unit
   * attribute.
   */
  std::unique_ptr<std::string> org;
  std::unique_ptr<std::string> email;
  std::unique_ptr<Address> address;
  std::unique_ptr<std::string> birthDay;
  std::unique_ptr<std::string> note;
  /**
   * A URL pointing to a website that represents the person in some way.
   */
  std::unique_ptr<std::string> url;
  // ### </MOBILE> ###

  std::unique_ptr<std::string> agent;
  std::unique_ptr<std::string> anniversary;
  /**
   * A URL to use for sending a scheduling request to the person's calendar.
   */
  std::unique_ptr<std::string> calAdrUri;
  /**
   * A URL to the person's calendar.
   */
  std::unique_ptr<std::string> calUri;
  std::vector<std::string> categories;
  /**
   * Describes the sensitivity of the information in the vCard.
   */
  std::unique_ptr<std::string> clazz;
  /**
   * Used for synchronizing different revisions of the same vCard.
   */
  std::unique_ptr<std::string> clientPidMap;
  /**
   * Defines a URL that shows when the person is "free" or "busy" on their calendar.
   */
  std::unique_ptr<std::string> fbUrl;
  /**
   * The formatted name std::string associated with the vCard object.
   */
  std::unique_ptr<std::string> formattedName;
  std::unique_ptr<std::string> gender;
  std::unique_ptr<std::string> geo;
  /**
   * Defines an instant messenger handle.
   * This property was introduced in a separate RFC when the latest vCard version was 3.0.
   * Therefore, 3.0 vCards might use this property without otherwise declaring it.
   */
  std::unique_ptr<std::string> impp;
  /**
   * The public encryption key associated with the vCard object. It may point to an external URL,
   * may be plain text, or may be embedded in the vCard as a Base64 encoded block of text.
   */
  std::unique_ptr<std::string> key;
  /**
   * Defines the type of entity that this vCard represents: 'application', 'individual', 'group',
   * 'location' or 'organization'; 'x-*' values may be used for experimental purposes.
   */
  std::unique_ptr<std::string> kind;
  /**
   * Represents the actual text that should be put on the mailing label when delivering a physical
   * package to the person/object associated with the vCard (related to the ADR property). Not
   * supported in version 4.0. Instead, this information is stored in the LABEL parameter of the ADR
   * property. Example: ADR;TYPE=home;LABEL="123 Main St\nNew York, NY 12345":;;123 Main St;New
   * York;NY;12345;USA
   */
  std::unique_ptr<std::string> label;
  /**
   * Defines a language that the person speaks.
   */
  std::unique_ptr<std::string> lang;
  std::unique_ptr<std::string> logo;
  /**
   * Type of email program used.
   */
  std::unique_ptr<std::string> mailer;
  /**
   * Defines a member that is part of the group that this vCard represents. Acceptable values
   * include: a "mailto:" URL containing an email address a UID which references the member's own
   * vCard The KIND property must be set to "group" in order to use this property.
   */
  std::unique_ptr<std::string> member;
  /**
   * An image or photograph of the individual associated with the vCard. It may point to an external
   * URL or may be embedded in the vCard as a Base64 encoded block of text.
   */
  std::unique_ptr<std::string> photo;
  /**
   * The identifier for the product that created the vCard object.
   */
  std::unique_ptr<std::string> prodId;
  /**
   * States that the vCard is a vCard.
   */
  std::unique_ptr<std::string> profile;
  /**
   * Another entity that the person is related to. Acceptable values include:
   * - a "mailto:" URL containing an email address
   * - a UID which references the person's own vCard
   */
  std::unique_ptr<std::string> related;
  /**
   * A timestamp for the last time the vCard was updated.
   */
  std::unique_ptr<std::string> rev;
  /**
   * The role, occupation, or business category of the vCard object within an organization.
   */
  std::unique_ptr<std::string> role;
  /**
   * Defines a std::string that should be used when an application sorts this vCard in some way.
   * Not supported in version 4.0. Instead, this information is stored in the SORT-AS parameter of
   * the N and/or ORG properties.
   */
  std::unique_ptr<std::string> sortString;
  /**
   * By default, if this property is not grouped with other properties it specifies the
   * pronunciation of the FN property of the vCard object. It may point to an external URL or may be
   * embedded in the vCard as a Base64 encoded block of text.
   */
  std::unique_ptr<std::string> sound;
  /**
   * A URL that can be used to get the latest version of this vCard.
   */
  std::unique_ptr<std::string> source;
  /**
   * The time zone of the vCard object.
   */
  std::unique_ptr<std::string> timeZone;
  /**
   * Specifies a value that represents a persistent, globally unique identifier associated with the
   * object.
   */
  std::unique_ptr<std::string> uid;
  /**
   * Any XML data that is attached to the vCard. This is used if the vCard was encoded in XML (xCard
   * standard) and the XML document contained elements which are not part of the xCard standard.
   */
  std::unique_ptr<std::string> xml;

  std::string str();

  friend std::ostream &operator<<(std::ostream &, VCard &);
};

} // namespace QrCodeUtil

#endif
