package com.hotel;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class Room {
    private final IntegerProperty roomNumber;
    private final StringProperty roomType;
    private final DoubleProperty price;
    private final StringProperty status;

    public Room(int roomNumber, String roomType, double price) {
        this.roomNumber = new SimpleIntegerProperty(roomNumber);
        this.roomType = new SimpleStringProperty(roomType);
        this.price = new SimpleDoubleProperty(price);
        this.status = new SimpleStringProperty("Available");
    }

    public static double getPriceByRoomNumber(ObservableList<Room> rooms, int roomNumber) {
        return rooms.stream()
                .filter(r -> r.getRoomNumber() == roomNumber)
                .map(Room::getPrice)
                .findFirst()
                .orElse(0.0);
    }

    public int getRoomNumber() { return roomNumber.get(); }
    public IntegerProperty roomNumberProperty() { return roomNumber; }
    public String getRoomType() { return roomType.get(); }
    public StringProperty roomTypeProperty() { return roomType; }
    public double getPrice() { return price.get(); }
    public DoubleProperty priceProperty() { return price; }
    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
    public void setStatus(String s) { this.status.set(s); }

    @Override
    public String toString() { return "Room " + getRoomNumber(); }
}