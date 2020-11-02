package io.github.michalczemierowski;

import io.github.michalczemierowski.model.Room;
import io.github.michalczemierowski.model.User;
import io.github.michalczemierowski.repository.RoomRepository;
import io.github.michalczemierowski.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class SharecodeApplication implements CommandLineRunner {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(SharecodeApplication.class, args);
    }

    @Override
    public void run(String... args) {
        User user1 = new User("michalc.jt@gmail.com", "jacekg");
        userRepository.save(user1);

        User user2 = new User("jacek1@wp.pl", "soplicalh");
        Room room = new Room(UUID.fromString("c6f10a73-a992-43e3-b0d3-91510cace478"), "test", user1, user2);

        User user3 = new User("jacek2@wp.pl", "soplicajl");

        Room room2 = new Room(UUID.fromString("c6f10a73-a992-43e3-b0d3-91510cace476"), "testhhj", user3, user1);
        room2.setContent("CONTENT1");

        StringBuilder content = new StringBuilder();
        for(int i = 0; i < 1000; i++)
        {
            content.append("content-");
        }

        room.setContent(content.toString());

        roomRepository.save(room2);
        roomRepository.save(room);
    }

}
