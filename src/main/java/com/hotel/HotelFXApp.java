package com.hotel;

import javafx.application.Application;
import javafx.collections.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

public class HotelFXApp extends Application {
    private ObservableList<Room> roomList = FXCollections.observableArrayList();
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private BookingController bookingCtrl;

    @Override
    public void start(Stage stage) throws Exception {
        FileHandler.loadRooms(roomList);
        FileHandler.loadCustomers(customerList);
        updateRoomStatuses();
        HotelFXAppHolder.setApp(this);

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // 1. Rooms Tab
        tabs.getTabs().add(new Tab("Rooms", createAdminRoomTab()));

        // 2. Booking Tab
        FXMLLoader bLoader = new FXMLLoader(getClass().getResource("/booking.fxml"));
        Parent bRoot = bLoader.load();
        bookingCtrl = bLoader.getController();
        bookingCtrl.initData(roomList, customerList);
        tabs.getTabs().add(new Tab("Booking", bRoot));

        // 3. Checkout Tab (Centered Glass Card)
        FXMLLoader cLoader = new FXMLLoader(getClass().getResource("/checkout.fxml"));
        Parent cRoot = cLoader.load();
        CheckoutController checkoutCtrl = cLoader.getController();
        checkoutCtrl.initData(roomList, customerList);

        StackPane checkoutCenterer = new StackPane(cRoot);
        checkoutCenterer.setAlignment(Pos.CENTER);
        
        cRoot.getStyleClass().add("glass-pane");
        if (cRoot instanceof Region) {
            Region card = (Region) cRoot;
            card.setMaxWidth(750); 
            card.setMaxHeight(400); // Increased for calendar
            card.setPadding(new Insets(40, 60, 40, 60));
        }
        tabs.getTabs().add(new Tab("Checkout", checkoutCenterer));

        // 4. Customers Tab
        tabs.getTabs().add(new Tab("Customers", createCustomerTab()));

        Scene scene = new Scene(tabs, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Hotel Management System");
        stage.show();
    }

    private VBox createAdminRoomTab() {
        VBox mainLayout = new VBox(35);
        mainLayout.setPadding(new Insets(40));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        VBox formWrapper = new VBox(25);
        formWrapper.getStyleClass().add("glass-pane");
        formWrapper.setMaxWidth(880);
        formWrapper.setPadding(new Insets(50, 50, 50, 90));
        formWrapper.setAlignment(Pos.TOP_LEFT);

        GridPane grid = new GridPane();
        grid.getStyleClass().add("glass-grid");
        grid.setHgap(40); grid.setVgap(25);

        TextField rNum = new TextField(); rNum.setPromptText("Enter Room Number");
        ComboBox<String> rType = new ComboBox<>();
        rType.getItems().addAll("Single", "Double", "Deluxe", "Suite");
        rType.setPromptText("Select Type");
        TextField rPrice = new TextField(); rPrice.setPromptText("Price per night");

        grid.add(new Label("Room No"), 0, 0); grid.add(rNum, 1, 0);
        grid.add(new Label("Type"), 0, 1); grid.add(rType, 1, 1);
        grid.add(new Label("Price"), 0, 2); grid.add(rPrice, 1, 2);

        Button addBtn = new Button("Add Room");
        addBtn.getStyleClass().add("button");
        VBox.setMargin(addBtn, new Insets(10, 0, 0, 105));
        
        addBtn.setOnAction(e -> {
            try {
                if(!rNum.getText().isEmpty() && rType.getValue() != null) {
                    roomList.add(new Room(Integer.parseInt(rNum.getText()), rType.getValue(), Double.parseDouble(rPrice.getText())));
                    FileHandler.saveRooms(roomList);
                    refreshAll();
                    rNum.clear(); rPrice.clear();
                }
            } catch (Exception ex) { }
        });

        formWrapper.getChildren().addAll(grid, addBtn);

        TableView<Room> table = new TableView<>(roomList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400); table.setMaxWidth(1000);
        TableColumn<Room, Integer> c1 = new TableColumn<>("Room");
        c1.setCellValueFactory(d -> d.getValue().roomNumberProperty().asObject());
        TableColumn<Room, String> c2 = new TableColumn<>("Type");
        c2.setCellValueFactory(d -> d.getValue().roomTypeProperty());
        TableColumn<Room, Double> c3 = new TableColumn<>("Price");
        c3.setCellValueFactory(d -> d.getValue().priceProperty().asObject());
        TableColumn<Room, String> c4 = new TableColumn<>("Status");
        c4.setCellValueFactory(d -> d.getValue().statusProperty());

        table.getColumns().addAll(c1, c2, c3, c4);
        mainLayout.getChildren().addAll(formWrapper, table);
        return mainLayout;
    }

    private VBox createCustomerTab() {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(40));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        TableView<Customer> table = new TableView<>(customerList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(600); table.setMaxWidth(1200);

        TableColumn<Customer, String> n = new TableColumn<>("Name");
        n.setCellValueFactory(d -> d.getValue().nameProperty());
        TableColumn<Customer, String> co = new TableColumn<>("Contact");
        co.setCellValueFactory(d -> d.getValue().contactProperty());
        TableColumn<Customer, Integer> r = new TableColumn<>("Room");
        r.setCellValueFactory(d -> d.getValue().roomNumberProperty().asObject());
        
        // Date Columns
        TableColumn<Customer, String> ci = new TableColumn<>("Check-In");
        ci.setCellValueFactory(d -> d.getValue().checkInProperty());
        TableColumn<Customer, String> cout = new TableColumn<>("Check-Out");
        cout.setCellValueFactory(d -> d.getValue().checkOutProperty());
        
        TableColumn<Customer, Integer> dy = new TableColumn<>("Days");
        dy.setCellValueFactory(d -> d.getValue().daysProperty().asObject());

        table.getColumns().addAll(n, co, r, ci, cout, dy);
        mainLayout.getChildren().addAll(table);
        return mainLayout;
    }

    public void updateRoomStatuses() {
        for (Room r : roomList) {
            boolean isOccupied = customerList.stream().anyMatch(c -> c.getRoomNumber() == r.getRoomNumber());
            r.setStatus(isOccupied ? "Occupied" : "Available");
        }
    }

    public void refreshAll() {
        updateRoomStatuses();
        if (bookingCtrl != null) {
            bookingCtrl.refreshRoomTypes();
            bookingCtrl.refreshAvailableRooms();
        }
    }

    public static void main(String[] args) { launch(args); }
}