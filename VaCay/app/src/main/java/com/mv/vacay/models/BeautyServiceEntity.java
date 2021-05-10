package com.mv.vacay.models;

/**
 * Created by a on 2/15/2017.
 */

public class BeautyServiceEntity {

    int _idx=0;

    String beautyName="";
    String beautySubName="";
    String beautyPrice="";
    String beautyDescription="";
    String beautyImageUrl="";
    int beautyImageRes=0;

    int _proIdx=0;
    int _serviceIdx=0;
    String firstName="";
    String lastName="";
    String fullName="";
    String providerPhotoUrl="";
    int providerResPhoto=0;
    String email="";
    String password="";
    String phone="";
    String city="";
    String location="";
    String companyName="";
    String token="";
    String enableTime="";
    String available="";

    String servicePercent="";
    String salary="";
    String productSalePercent="";

    String providerTakeHome="";
    String managerTakeHome="";

    public BeautyServiceEntity(){

    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

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

    public void setServicePercent(String servicePercent) {
        this.servicePercent = servicePercent;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public void setProductSalePercent(String productSalePercent) {
        this.productSalePercent = productSalePercent;
    }

    public String getServicePercent() {
        return servicePercent;
    }

    public String getSalary() {
        return salary;
    }

    public String getProductSalePercent() {
        return productSalePercent;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public void setEnableTime(String enableTime) {
        this.enableTime = enableTime;
    }

    public String getEnableTime() {
        return enableTime;
    }

    public int get_serviceIdx() {
        return _serviceIdx;
    }

    public void set_serviceIdx(int _serviceIdx) {
        this._serviceIdx = _serviceIdx;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void set_proIdx(int _proIdx) {
        this._proIdx = _proIdx;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setProviderPhotoUrl(String providerPhotoUrl) {
        this.providerPhotoUrl = providerPhotoUrl;
    }

    public void setProviderResPhoto(int providerResPhoto) {
        this.providerResPhoto = providerResPhoto;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int get_proIdx() {
        return _proIdx;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName+" "+lastName;
    }

    public String getProviderPhotoUrl() {
        return providerPhotoUrl;
    }

    public int getProviderResPhoto() {
        return providerResPhoto;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCity() {
        return city;
    }

    public String getLocation() {
        return location;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void set_idx(int _idx) {
        this._idx = _idx;
    }

    public void setBeautyName(String beautyName) {
        this.beautyName = beautyName;
    }

    public void setBeautySubName(String beautySubName) {
        this.beautySubName = beautySubName;
    }

    public void setBeautyPrice(String beautyPrice) {
        this.beautyPrice = beautyPrice;
    }

    public void setBeautyImageUrl(String beautyImageUrl) {
        this.beautyImageUrl = beautyImageUrl;
    }

    public void setBeautyDescription(String beautyDescription) {
        this.beautyDescription = beautyDescription;
    }

    public void setBeautyImageRes(int beautyImageRes) {
        this.beautyImageRes = beautyImageRes;
    }

    public int get_idx() {
        return _idx;
    }

    public String getBeautyName() {
        return beautyName;
    }

    public String getBeautySubName() {
        return beautySubName;
    }

    public String getBeautyPrice() {
        return beautyPrice;
    }

    public String getBeautyDescription() {
        return beautyDescription;
    }

    public String getBeautyImageUrl() {
        return beautyImageUrl;
    }

    public int getBeautyImageRes() {
        return beautyImageRes;
    }
}
