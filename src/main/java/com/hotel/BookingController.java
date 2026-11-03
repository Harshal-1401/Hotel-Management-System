package com.hotel;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingController {
    @FXML private TextField nameField, contactField;
    @FXML private DatePicker checkInPicker, checkOutPicker;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<Room> availableRooms;
    @FXML private ComboBox<Customer> cancelDropdown;
    @FXML private Label bookingStatus, cancelStatus;

    private ObservableList<Room> roomList;
    private ObservableList<Customer> customerList;

    public void initData(ObservableList<Room> rooms, ObservableList<Customer> customers) {
        this.roomList = rooms;
        this.customerList = customers;
        setupComboBoxDisplays();
        setupDateRestrictions();
        refreshRoomTypes();
        cancelDropdown.setItems(customerList);
        typeFilter.setOnAction(e -> refreshAvailableRooms());
    }

    private void setupComboBoxDisplays() {
        // Room Dropdown: Now shows Room No and Price
        Callback<ListView<Room>, ListCell<Room>> roomFactory = lv -> new ListCell<>() {
            @Override protected void updateItem(Room item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : 
                    "Room " + item.getRoomNumber() + " (₹" + item.getPrice() + "/day)");
            }
        };
        availableRooms.setCellFactory(roomFactory);
        availableRooms.setButtonCell(roomFactory.call(null));

        // Cancellation Dropdown: Now shows Name, Room No, and Stay Period
        Callback<ListView<Customer>, ListCell<Customer>> custFactory = lv -> new ListCell<>() {
            @Override protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : 
                    item.getName() + " [Room " + item.getRoomNumber() + "] | Stay: " + item.getCheckIn() + " to " + item.getCheckOut());
            }
        };
        cancelDropdown.setCellFactory(custFactory);
        cancelDropdown.setButtonCell(custFactory.call(null));
    }

    private void setupDateRestrictions() {
        checkInPicker.setDayCellFactory(p -> new DateCell() {
            @Override public void updateItem(LocalDate d, boolean e) {
                super.updateItem(d, e); setDisable(e || d.isBefore(LocalDate.now()));
            }
        });
        checkOutPicker.setDayCellFactory(p -> new DateCell() {
            @Override public void updateItem(LocalDate d, boolean e) {
                super.updateItem(d, e);
                LocalDate start = checkInPicker.getValue();
                setDisable(e || d.isBefore(start != null ? start.plusDays(1) : LocalDate.now().plusDays(1)));
            }
        });
    }

    public void refreshRoomTypes() { typeFilter.getItems().setAll("Single", "Double", "Deluxe", "Suite"); }
    
    public void refreshAvailableRooms() {
        String type = typeFilter.getValue();
        if (type != null) {
            availableRooms.getItems().setAll(roomList.stream().filter(r -> r.getRoomType().equals(type) && r.getStatus().equals("Available")).toList());
        }
    }

    @FXML
    private void handleBooking() {
        if (nameField.getText().isEmpty() || contactField.getText().isEmpty() || availableRooms.getValue() == null) return;

        String contact = contactField.getText();

        // Phone number validation (must be exactly 10 digits)
        if (!contact.matches("\\d{10}")) {
            showTimedMessage(bookingStatus, "❌ Invalid phone number (must be 10 digits)");
            return;
        }
        
        Room room = availableRooms.getValue();
        LocalDate in = checkInPicker.getValue();
        LocalDate out = checkOutPicker.getValue();
        long days = ChronoUnit.DAYS.between(in, out);

        customerList.add(new Customer(nameField.getText(), contact, room.getRoomNumber(), (int)days, in.toString(), out.toString()));
        FileHandler.saveCustomers(customerList);
        HotelFXAppHolder.getApp().refreshAll();
        
        showTimedMessage(bookingStatus, "✅ Room " + room.getRoomNumber() + " Booked!");
        clearFields();
    }

    @FXML
    private void handleCancellation() {
        Customer sel = cancelDropdown.getValue();
        if (sel != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Cancel booking for " + sel.getName() + "?", ButtonType.YES, ButtonType.NO);
            if (alert.showAndWait().get() == ButtonType.YES) {
                customerList.remove(sel);
                FileHandler.saveCustomers(customerList);
                HotelFXAppHolder.getApp().refreshAll();
                showTimedMessage(cancelStatus, "❌ Cancelled Successfully.");
                cancelDropdown.getSelectionModel().clearSelection();
            }
        }
    }

    private void showTimedMessage(Label label, String msg) {
        label.setText(msg);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> label.setText(""));
        pause.play();
    }

    private void clearFields() {
        nameField.clear(); contactField.clear();
        typeFilter.getSelectionModel().clearSelection();
        availableRooms.getSelectionModel().clearSelection();
        checkInPicker.setValue(null); checkOutPicker.setValue(null);
    }
}