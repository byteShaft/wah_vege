package com.byteshaft.wahwege.gettersetter;

import java.util.ArrayList;

/**
 * Created by husnain on 11/20/17.
 */

public class OrderHistoryMain {

    private ArrayList<OrderHistoryItmes> arrayList;
    private String deliveryStatus;
    private String grandTotal;
    private String deliveryDate;

    public ArrayList<OrderHistoryItmes> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<OrderHistoryItmes> arrayList) {
        this.arrayList = arrayList;
    }


    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }


    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
