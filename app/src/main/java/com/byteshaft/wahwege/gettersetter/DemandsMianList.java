package com.byteshaft.wahwege.gettersetter;

import java.util.ArrayList;

/**
 * Created by husnain on 11/20/17.
 */

public class DemandsMianList {

    private ArrayList<DemandsItemsList> arrayList;
    private String deliveryDate;

    public ArrayList<DemandsItemsList> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<DemandsItemsList> arrayList) {
        this.arrayList = arrayList;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
