package com.mv.vacay.models;

/**
 * Created by a on 4/10/2017.
 */

public class AnnouncementEntity {

    String idx="0";
    String adminId="0";
    String title="";
    String audience="";
    String subject="";
    String description="";
    String callofAction="";
    String messageOwnerEmail="";
    String pictureUrl="";
    String views="";
    String responses="";
    String company="";
    String logoUrl="";
    String postDate="";
    String survey="";

    public AnnouncementEntity(){

    }

    public void setSurvey(String survey) {
        this.survey = survey;
    }

    public String getSurvey() {
        return survey;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCallofAction(String callofAction) {
        this.callofAction = callofAction;
    }

    public void setMessageOwnerEmail(String messageOwnerEmail) {
        this.messageOwnerEmail = messageOwnerEmail;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public void setResponses(String responses) {
        this.responses = responses;
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

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getAudience() {
        return audience;
    }

    public String getDescription() {
        return description;
    }

    public String getCallofAction() {
        return callofAction;
    }

    public String getMessageOwnerEmail() {
        return messageOwnerEmail;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getViews() {
        return views;
    }

    public String getResponses() {
        return responses;
    }

    public String getCompany() {
        return company;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}
