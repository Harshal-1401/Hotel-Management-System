package com.hotel;

public class HotelFXAppHolder {

    private static HotelFXApp app;

    public static void setApp(HotelFXApp a) {
        app = a;
    }

    public static HotelFXApp getApp() {
        return app;
    }
}
