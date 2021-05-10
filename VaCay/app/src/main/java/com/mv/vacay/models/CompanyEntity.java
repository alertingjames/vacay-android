package com.mv.vacay.models;

/**
 * Created by a on 4/11/2017.
 */

public class CompanyEntity {

    String idx="0";
    String adminId="";
    String company="";
    String logoUrl="";

    public CompanyEntity(){

    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getIdx() {
        return idx;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getCompany() {
        return company;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}
