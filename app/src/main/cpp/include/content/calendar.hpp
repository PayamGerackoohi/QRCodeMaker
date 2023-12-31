#ifndef CALENDAR_HPP
#define CALENDAR_HPP

#include <ostream>
#include <string>

namespace QrCodeUtil {
struct Calendar {
  struct Event {
    /**
     * This property specifies the persistent, globally unique identifier for the iCalendar object.
     */
    std::unique_ptr<std::string> uid;
    /**
     * The property indicates the date/time that the instance of the iCalendar object was created.
     * Sample: 19970714T170000Z
     */
    std::unique_ptr<std::string> creationDate;
    /**
     * Sample: CN=John Doe:MAILTO:john.doe@example.com
     */
    std::unique_ptr<std::string> organizer;
    std::unique_ptr<std::string> summary;
    /**
     * Sample: 20180601T070000Z
     */
    std::unique_ptr<std::string> start;
    /**
     * Sample: 20180831T070000Z
     */
    std::unique_ptr<std::string> end;
    std::unique_ptr<std::string> geo;

    friend std::ostream &operator<<(std::ostream &, Calendar::Event &);
  };

  std::unique_ptr<std::string> version;
  /**
   * This property specifies the identifier for the product that created the iCalendar object.
   */
  std::unique_ptr<std::string> prodId;
  std::unique_ptr<Event> event;

  friend std::ostream &operator<<(std::ostream &, Calendar &);
  std::string str();
};
} // namespace QrCodeUtil

#endif
