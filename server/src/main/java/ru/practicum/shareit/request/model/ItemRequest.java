package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Описание запроса не может быть пустым.")
    @Column
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User requester; //пользователь, создавший запрос

    @Column(name = "created")
    private LocalDateTime created;
}