package com.conference.lecture;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class Lecture {
    private String lectureId;
    private String title;
    private Room room;
    private Date date;
    private Time time;
    private int duration;
    private int attends;

    public Lecture(String lectureId, String title, int attends) {
        this.lectureId = lectureId;
        this.title = title;
        this.attends = attends;
        room = new Room("","", "", 0);
        date = Date.valueOf(LocalDate.now());
        time = Time.valueOf(LocalTime.now());
        duration = 0;
    }

    public Lecture(String lectureId, String title, Room room, Date date, Time time, int duration) {
        this.lectureId = lectureId;
        this.title = title;
        this.room = room;
        this.date = date;
        this.time = time;
        this.duration = duration;
        attends = 0;
    }

    public String getLectureId() {
        return lectureId;
    }

    public String getTitle() {
        return title;
    }

    public Room getRoom() {
        return room;
    }

    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public int getDuration() {
        return duration;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getAttends() {
        return attends;
    }
}
