package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"booker", "item"})
@Builder
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_time", nullable = false)
    private LocalDateTime start;
    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "booker_id", nullable = false)
    private User booker;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @Enumerated(EnumType.STRING)
    @JoinColumn(name = "status", nullable = false)
    private Status status;
}
