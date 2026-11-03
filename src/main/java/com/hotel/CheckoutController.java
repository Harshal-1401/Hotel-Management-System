package com.hotel;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.util.Callback;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.text.NumberFormat;
import java.util.Locale;

public class CheckoutController {
    @FXML private ComboBox<Customer> occupiedRoomCombo;
    @FXML private DatePicker actualCheckoutPicker;
    @FXML private Label checkoutStatus;

    private ObservableList<Customer> customerList;
    private ObservableList<Room> roomList;

    public void initData(ObservableList<Room> rooms, ObservableList<Customer> customers) {
        this.roomList = rooms;
        this.customerList = customers;
        occupiedRoomCombo.setItems(customerList);

        // Checkout Dropdown: Updated Label (Name + Room + Stay Period)
        Callback<ListView<Customer>, ListCell<Customer>> custFactory = lv -> new ListCell<>() {
            @Override protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : 
                    item.getName() + " (Room " + item.getRoomNumber() + ") | Stay: " + item.getCheckIn() + " to " + item.getCheckOut());
            }
        };
        occupiedRoomCombo.setCellFactory(custFactory);
        occupiedRoomCombo.setButtonCell(custFactory.call(null));

        // Logic fix for date restrictions
        occupiedRoomCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                LocalDate checkInDate = LocalDate.parse(newVal.getCheckIn());
                actualCheckoutPicker.setDayCellFactory(p -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(empty || date.isBefore(checkInDate));
                    }
                });
                actualCheckoutPicker.setValue(null);
            }
        });
    }

    @FXML
    private void handleCheckout() {
        Customer sel = occupiedRoomCombo.getValue();
        LocalDate actualOut = actualCheckoutPicker.getValue();
        
        if (sel == null || actualOut == null) return;

        LocalDate checkInDate = LocalDate.parse(sel.getCheckIn());
        long actualStay = ChronoUnit.DAYS.between(checkInDate, actualOut);
        if (actualStay <= 0) actualStay = 1; 

        double rate = Room.getPriceByRoomNumber(roomList, sel.getRoomNumber());
        double total = rate * actualStay;
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Final Invoice");
        alert.setHeaderText("Guest Receipt - Room " + sel.getRoomNumber());

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(15));

        grid.add(new Label("Guest Name:"), 0, 0); grid.add(new Label(sel.getName()), 1, 0);
        grid.add(new Label("Contact:"), 0, 1); grid.add(new Label(sel.getContact()), 1, 1);
        grid.add(new Label("Check-In:"), 0, 2); grid.add(new Label(sel.getCheckIn()), 1, 2);
        grid.add(new Label("Actual Check-Out:"), 0, 3); grid.add(new Label(actualOut.toString()), 1, 3);
        grid.add(new Label("Total Stay:"), 0, 4); grid.add(new Label(actualStay + " Day(s)"), 1, 4);

        Label totalLbl = new Label("TOTAL AMOUNT:");
        totalLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        Label priceLbl = new Label(currency.format(total));
        priceLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #2c3e50;");
        grid.add(totalLbl, 0, 5); grid.add(priceLbl, 1, 5);

        alert.getDialogPane().setContent(grid);

        ButtonType payBtn = new ButtonType("Confirm Payment");
        ButtonType backBtn = new ButtonType("Back", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(payBtn, backBtn);

        if (alert.showAndWait().get() == payBtn) {
            customerList.remove(sel);
            FileHandler.saveCustomers(customerList);
            HotelFXAppHolder.getApp().refreshAll();
            showTimedMessage("✨ Checkout Complete for Room " + sel.getRoomNumber());
            actualCheckoutPicker.setValue(null);
            occupiedRoomCombo.getSelectionModel().clearSelection();
        }
    }

    private void showTimedMessage(String msg) {
        checkoutStatus.setText(msg);
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> checkoutStatus.setText(""));
        pause.play();
    }
}