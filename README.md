# Hotel Management System

A desktop app for running the front desk of a small hotel: manage rooms, book and cancel stays, check guests out with an itemised bill, and view all current guests. Built with **JavaFX 21** and **Maven**, with data saved to plain text files so it persists between runs.


## Features

**Rooms**
- Add rooms with a number, type (Single, Double, Deluxe, Suite) and nightly price
- Live table of all rooms, with each one's status (*Available* / *Occupied*) worked out from current bookings

**Booking**
- Pick a room type, and the room list shows only rooms of that type that are free
- Date pickers block past dates, and check-out can't be on or before check-in
- Phone numbers must be exactly 10 digits
- Cancel any active booking from a dropdown, with a confirmation dialog

**Checkout**
- Choose a guest and their actual check-out date (which can differ from the booked one)
- Generates an invoice showing guest details, nights stayed and the total in ₹, calculated from the room's nightly rate
- Confirming payment frees the room immediately

**Customers**
- Table of every current guest: name, contact, room, check-in, check-out and length of stay

## Tech stack

| | |
|---|---|
| Language | Java 21 |
| UI | JavaFX 21 (Controls + FXML), custom CSS dark theme |
| Build | Maven, `javafx-maven-plugin` |
| Storage | CSV-style text files (`rooms.txt`, `customers.txt`) |

## Running it

You need **JDK 21** and **Maven**. JavaFX is downloaded automatically through `pom.xml`, so there's nothing else to install.

```bash
git clone https://github.com/Harshal-1401/Hotel-Management-System.git
cd Hotel-Management-System
mvn javafx:run
```

The repo includes sample rooms and test bookings so the app has data on first launch. Run it from the project root, because the data files are read from the current directory.

## Project structure

```
src/main/java/com/hotel/
├── HotelFXApp.java          # Entry point; builds the tabs, holds shared state
├── BookingController.java   # Booking + cancellation logic (booking.fxml)
├── CheckoutController.java  # Checkout + invoice (checkout.fxml)
├── Room.java, Customer.java # Models using JavaFX properties
├── FileHandler.java         # Load/save rooms and customers
└── HotelFXAppHolder.java    # Lets controllers trigger a refresh across tabs
src/main/resources/
├── booking.fxml, checkout.fxml
└── Style.css
```

## How it works

All four tabs share the same two `ObservableList`s (rooms and customers). JavaFX tables bound to these lists update automatically, so a booking made on one tab shows up in the Rooms and Customers tabs without reloading. Room status isn't stored; it's recalculated from the customer list after every booking, cancellation or checkout, so it can't go out of sync with the actual bookings. Every change is written straight to disk.

The Rooms and Customers tabs are built in Java code, while Booking and Checkout use FXML layouts with their own controller classes. That split was deliberate, to try out both approaches in one project.

## Known limitations and next steps

- **Status is not date-aware.** A booking for next week marks the room *Occupied* today. The next step is to check for overlapping date ranges, so a room can hold several future bookings.
- **Plain-text storage.** A guest name containing a comma would break the file format. Moving to SQLite (or at least a proper CSV library) would fix this and allow booking history after checkout.
- **Silent input errors.** Adding a room with a duplicate number or a non-numeric price fails without telling the user. It should show an error message instead.
- **No tests.** Booking and billing logic sits inside the UI controllers. Pulling it into a separate service class would make it unit-testable.
