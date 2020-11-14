package io.github.michalczemierowski.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_messages")
public class RoomMessage {
    @Id
    @GeneratedValue
    public int id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    public Room room;

    @Column(length = 255, nullable = false)
    public String content;

    @Column(name = "send_datetime", nullable = false)
    public LocalDateTime sendDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;

    public RoomMessage() {
    }

    public RoomMessage(User user, Room room, String content) {
        this.user = user;
        this.room = room;
        this.content = content;
        this.sendDateTime = LocalDateTime.now();
    }

    public RoomMessage(User user, Room room, String content, LocalDateTime sendDateTime) {
        this.user = user;
        this.room = room;
        this.content = content;
        this.sendDateTime = sendDateTime;
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
