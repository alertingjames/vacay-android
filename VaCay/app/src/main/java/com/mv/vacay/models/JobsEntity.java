package com.mv.vacay.models;

/**
 * Created by a on 4/10/2017.
 */

public class JobsEntity {

    String idx="0";
    String adminId="0";
    String jobName="";
    String jobReqId="";
    String Department="";
    String location="";
    String description="";
    String postingDate="";
    String emptyField="";
    String company="";
    String logoUrl="";
    String survey="";

    public JobsEntity(){

    }

    public String getSurvey() {
        return survey;
    }

    public void setSurvey(String survey) {
        this.survey = survey;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public void setJobReqId(String jobReqId) {
        this.jobReqId = jobReqId;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public void setEmptyField(String emptyField) {
        this.emptyField = emptyField;
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

    public String getJobName() {
        return jobName;
    }

    public String getJobReqId() {
        return jobReqId;
    }

    public String getDepartment() {
        return Department;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public String getEmptyField() {
        return emptyField;
    }

    public String getCompany() {
        return company;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}
