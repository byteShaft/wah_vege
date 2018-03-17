package com.byteshaft.wahwege.gettersetter;

/**
 * Created by husnain on 11/6/17.
 */

public class VegetablesFruits {

    private String productName;
    private int productId;
    private String productImage;
    private String productMarketPrice;
    private String productWahVegePrice;
    private String productStockCount;
    private String productunit;

    public String getProductunit() {
        return productunit;
    }

    public void setProductunit(String productunit) {
        this.productunit = productunit;
    }


    public String getProductStockCount() {
        return productStockCount;
    }

    public void setProductStockCount(String productStockCount) {
        this.productStockCount = productStockCount;
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


    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductMarketPrice() {
        return productMarketPrice;
    }

    public void setProductMarketPrice(String productMarketPrice) {
        this.productMarketPrice = productMarketPrice;
    }

    public String getProductWahVegePrice() {
        return productWahVegePrice;
    }

    public void setProductWahVegePrice(String productWahVegePrice) {
        this.productWahVegePrice = productWahVegePrice;
    }
}
