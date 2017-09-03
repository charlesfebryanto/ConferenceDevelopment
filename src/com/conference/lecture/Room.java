package com.conference.lecture;

public class Room {
    private String roomId;
    private String name;
    private String description;
    private int seat;

    public Room(String roomId, String name, String description, int seat) {
        this.roomId = roomId;
        this.name = name;
        this.description = description;
        this.seat = seat;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getSeat() {
        return seat;
    }
}
