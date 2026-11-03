package com.hotel;

import javafx.beans.property.*;

public class Customer {
    private final StringProperty name;
    private final StringProperty contact;
    private final IntegerProperty roomNumber;
    private final IntegerProperty days;
    private final StringProperty checkIn;
    private final StringProperty checkOut;

    public Customer(String name, String contact, int roomNumber, int days, String checkIn, String checkOut) {
        this.name = new SimpleStringProperty(name);
        this.contact = new SimpleStringProperty(contact);
        this.roomNumber = new SimpleIntegerProperty(roomNumber);
        this.days = new SimpleIntegerProperty(days);
        this.checkIn = new SimpleStringProperty(checkIn);
        this.checkOut = new SimpleStringProperty(checkOut);
    }

    // Standard Getters/Properties
    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public String getContact() { return contact.get(); }
    public StringProperty contactProperty() { return contact; }
    public int getRoomNumber() { return roomNumber.get(); }
    public IntegerProperty roomNumberProperty() { return roomNumber; }
    public int getDays() { return days.get(); }
    public IntegerProperty daysProperty() { return days; }
    public String getCheckIn() { return checkIn.get(); }
    public StringProperty checkInProperty() { return checkIn; }
    public String getCheckOut() { return checkOut.get(); }
    public StringProperty checkOutProperty() { return checkOut; }

    @Override
    public String toString() {
        return getName() + " (Room " + getRoomNumber() + ")";
    }
}