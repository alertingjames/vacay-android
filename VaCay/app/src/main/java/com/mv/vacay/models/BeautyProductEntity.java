package com.mv.vacay.models;

/**
 * Created by a on 2/15/2017.
 */

public class BeautyProductEntity {
    int _idx=0;
    int _itemId=0;
    String brand="";
    String product="";
    String productName="";
    String size="";
    String price="";
    String description="";
    String productImageUrl="";
    int productImageRes=0;
    String companyName="";
    String location="";
    String inventoryNumber="";
    String providerName="";
    String email="";

    String providerTakeHome="";
    String managerTakeHome="";

    public  BeautyProductEntity(){}

    public void setProviderTakeHome(String providerTakeHome) {
        this.providerTakeHome = providerTakeHome;
    }

    public void setManagerTakeHome(String managerTakeHome) {
        this.managerTakeHome = managerTakeHome;
    }

    public String getProviderTakeHome() {
        return providerTakeHome;
    }

    public String getManagerTakeHome() {
        return managerTakeHome;
    }

    public int get_itemId() {
        return _itemId;
    }

    public void set_itemId(int _itemId) {
        this._itemId = _itemId;
    }

    public void set_idx(int _idx) {
        this._idx = _idx;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public void setProductImageRes(int productImageRes) {
        this.productImageRes = productImageRes;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setInventoryNumber(String inventoryNumber) {
        this.inventoryNumber = inventoryNumber;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSaleStatus(String saleStatus) {
        this.saleStatus = saleStatus;
    }

    public int get_idx() {
        return _idx;
    }

    public String getBrand() {
        return brand;
    }

    public String getProduct() {
        return product;
    }

    public String getProductName() {
        return productName;
    }

    public String getSize() {
        return size;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public int getProductImageRes() {
        return productImageRes;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getLocation() {
        return location;
    }

    public String getInventoryNumber() {
        return inventoryNumber;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getEmail() {
        return email;
    }

    public String getSaleStatus() {
        return saleStatus;
    }

    String saleStatus="";
}
