package ru.practicum.shareit.comment.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;
    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @OneToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    @Column(name = "created")
    private LocalDateTime created;

    public Comment() {

    }
}
