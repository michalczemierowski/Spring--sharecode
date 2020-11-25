package io.github.michalczemierowski.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_messages")
public class RoomMessage {
    @Id
    @GeneratedValue
    private int id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(length = 1024, nullable = false)
    private String content;

    @Column(name = "send_datetime", nullable = false)
    private LocalDateTime sendDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public RoomMessage() {
        this.sendDateTime = LocalDateTime.now();
    }

    public RoomMessage(User user, Room room, String content) {
        this.user = user;
        this.room = room;
        this.content = content;
        this.sendDateTime = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSendDateTime() {
        return sendDateTime;
    }

    public User getUser() {
        return user;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSendDateTime(LocalDateTime sendDateTime) {
        this.sendDateTime = sendDateTime;
    }
}
