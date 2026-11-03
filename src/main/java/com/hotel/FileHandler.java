package com.hotel;

import javafx.collections.ObservableList;
import java.io.*;
import java.util.List;

public class FileHandler {

    public static void saveRooms(List<Room> list) {
        try (PrintWriter out = new PrintWriter(new FileWriter("rooms.txt"))) {
            for (Room r : list) {
                out.println(r.getRoomNumber() + "," + r.getRoomType() + "," + r.getPrice());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void loadRooms(ObservableList<Room> list) {
        File file = new File("rooms.txt");
        if (!file.exists()) return;
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length == 3) {
                    list.add(new Room(Integer.parseInt(p[0]), p[1], Double.parseDouble(p[2])));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void saveCustomers(List<Customer> list) {
        try (PrintWriter out = new PrintWriter(new FileWriter("customers.txt"))) {
            for (Customer c : list) {
                out.println(c.getName() + "," + c.getContact() + "," + c.getRoomNumber() + "," + 
                            c.getDays() + "," + c.getCheckIn() + "," + c.getCheckOut());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void loadCustomers(ObservableList<Customer> list) {
        File file = new File("customers.txt");
        if (!file.exists()) return;
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length == 6) {
                    list.add(new Customer(p[0], p[1], Integer.parseInt(p[2]), 
                                          Integer.parseInt(p[3]), p[4], p[5]));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}