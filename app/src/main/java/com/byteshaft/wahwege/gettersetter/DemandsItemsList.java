package com.byteshaft.wahwege.gettersetter;

/**
 * Created by husnain on 11/20/17.
 */

public class DemandsItemsList {


    private int productId;
    private String productName;
    private String productUnit;
    private float productQuantity;

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public float getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(float productQuantity) {
        this.productQuantity = productQuantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

}
