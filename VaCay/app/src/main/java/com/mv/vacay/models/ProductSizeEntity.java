package com.mv.vacay.models;

/**
 * Created by a on 3/31/2017.
 */

public class ProductSizeEntity {
    String idx="0";
    String productSize="";
    String productQuantity="";
    String productPrice="";

    public ProductSizeEntity(){

    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getIdx() {
        return idx;
    }

    public String getProductSize() {
        return productSize;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public String getProductPrice() {
        return productPrice;
    }
}
