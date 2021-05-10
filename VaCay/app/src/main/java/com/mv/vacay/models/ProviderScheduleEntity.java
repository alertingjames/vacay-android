package com.mv.vacay.models;

/**
 * Created by a on 3/27/2017.
 */

public class ProviderScheduleEntity {
    String scheduleId="0";
    String proId="0";
    String scheduleStart="";
    String scheduleEnd="";
    String scheduleComment="";

    public ProviderScheduleEntity(){

    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public void setScheduleStart(String scheduleStart) {
        this.scheduleStart = scheduleStart;
    }

    public void setScheduleEnd(String scheduleEnd) {
        this.scheduleEnd = scheduleEnd;
    }

    public void setScheduleComment(String scheduleComment) {
        this.scheduleComment = scheduleComment;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getProId() {
        return proId;
    }

    public String getScheduleStart() {
        return scheduleStart;
    }

    public String getScheduleEnd() {
        return scheduleEnd;
    }

    public String getScheduleComment() {
        return scheduleComment;
    }
}

