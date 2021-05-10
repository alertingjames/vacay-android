package com.mv.vacay.models;

import android.graphics.Bitmap;

/**
 * Created by a on 3/28/2017.
 */

public class BroadmoorEntity {
    String idx="0";
    String adminId="0";
    String adminEmail="";
    String productImageUrl="";
    String productName="";
    String productSize="";
    String productPrice="";
    String productInventory="";
    String productCategory="";
    String productAdditional="";
    int productImageRes=0;
    int broadmoorImageRes=0;
    String adminStripeAccountId="";
    String broadmoorLogoUrl="";
    String productQuantity="";
    Bitmap productImageBitmap=null;
    Bitmap broadmoorLogoBitmap=null;

    public BroadmoorEntity(){

    }

    public void setProductImageBitmap(Bitmap productImageBitmap) {
        this.productImageBitmap = productImageBitmap;
    }

    public void setBroadmoorLogoBitmap(Bitmap broadmoorLogoBitmap) {
        this.broadmoorLogoBitmap = broadmoorLogoBitmap;
    }

    public Bitmap getProductImageBitmap() {
        return productImageBitmap;
    }

    public Bitmap getBroadmoorLogoBitmap() {
        return broadmoorLogoBitmap;
    }

    public void setBroadmoorImageRes(int broadmoorImageRes) {
        this.broadmoorImageRes = broadmoorImageRes;
    }

    public int getBroadmoorImageRes() {
        return broadmoorImageRes;
    }

    public void setBroadmoorLogoUrl(String broadmoorLogoUrl) {
        this.broadmoorLogoUrl = broadmoorLogoUrl;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getBroadmoorLogoUrl() {
        return broadmoorLogoUrl;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setAdminStripeAccountId(String adminStripeAccountId) {
        this.adminStripeAccountId = adminStripeAccountId;
    }

    public String getAdminStripeAccountId() {
        return adminStripeAccountId;
    }

    public void setProductImageRes(int productImageRes) {
        this.productImageRes = productImageRes;
    }

    public int getProductImageRes() {
        return productImageRes;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public void setProductInventory(String productInventory) {
        this.productInventory = productInventory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public void setProductAdditional(String productAdditional) {
        this.productAdditional = productAdditional;
    }

    public String getIdx() {
        return idx;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductSize() {
        return productSize;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductInventory() {
        return productInventory;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getProductAdditional() {
        return productAdditional;
    }
}
