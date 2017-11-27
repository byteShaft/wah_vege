package com.byteshaft.wahwege.gettersetter;

/**
 * Created by husnain on 11/13/17.
 */

public class Orders {


    private int productId;
    private float productQuantity;
    private int productPrice;
    private String productName;
    private String productImage;

    public String getWahvegePrice() {
        return wahvegePrice;
    }

    public void setWahvegePrice(String wahvegePrice) {
        this.wahvegePrice = wahvegePrice;
    }

    private String wahvegePrice;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public float getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(float productQuantity) {
        this.productQuantity = productQuantity;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
}
