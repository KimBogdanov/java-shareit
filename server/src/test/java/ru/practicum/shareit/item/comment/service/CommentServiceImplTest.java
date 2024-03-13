//package ru.practicum.shareit.item.comment.service;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import ru.practicum.shareit.booking.model.Booking;
//import ru.practicum.shareit.booking.model.enums.Status;
//import ru.practicum.shareit.booking.repository.BookingRepository;
//import ru.practicum.shareit.exception.CommentNotAllowedException;
//import ru.practicum.shareit.exception.NotAvailableException;
//import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
//import ru.practicum.shareit.item.comment.model.Comment;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.repository.ItemRepository;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//
//import javax.persistence.EntityManager;
//import javax.persistence.TypedQuery;
//import java.time.Duration;
//import java.time.LocalDateTime;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.hamcrest.CoreMatchers.notNullValue;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.equalTo;
//
//@Transactional
//@SpringBootTest(
//        properties = "db.name = test",
//        webEnvironment = SpringBootTest.WebEnvironment.NONE
//)
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//class CommentServiceImplTest {
//    private final CommentService commentService;
//    private final UserRepository userRepository;
//    private final ItemRepository itemRepository;
//    private final BookingRepository bookingRepository;
//    private final EntityManager em;
//    private User user1;
//    private User user2;
//    private User user3;
//    private Item item2;
//
//    @BeforeEach
//    void setUp() {
//        User user1 = User.builder()
//                .name("John")
//                .email("first@mail.com")
//                .build();
//        User user2 = User.builder()
//                .name("Max")
//                .email("second@mail.com")
//                .build();
//        User user3 = User.builder()
//                .name("Ivan")
//                .email("third@mail.com")
//                .build();
//
//        this.user1 = userRepository.save(user1);
//        this.user2 = userRepository.save(user2);
//        this.user3 = userRepository.save(user3);
//
//        Item item2 = Item.builder()
//                .name("item2")
//                .description("descr2")
//                .available(true)
//                .owner(this.user2).build();
//
//        this.item2 = itemRepository.save(item2);
//
//        Booking booking = Booking.builder()
//                .start(LocalDateTime.now().minus(Duration.ofDays(2)))
//                .end(LocalDateTime.now().minus(Duration.ofDays(1)))
//                .booker(user1)
//                .item(item2)
//                .status(Status.APPROVED)
//                .build();
//        bookingRepository.save(booking);
//    }
//
//    @Test
//    @DisplayName("Добавляем comment")
//    void saveComment() {
//        CommentCreateDto commentDto = CommentCreateDto.builder().text("SomeText").build();
//        commentService.saveComment(user1.getId(), item2.getId(), commentDto);
//
//        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.text = :text", Comment.class);
//        Comment comment = query.setParameter("text", commentDto.getText())
//                .getSingleResult();
//
//        assertThat(comment.getId(), notNullValue());
//        assertThat(comment.getText(), equalTo(commentDto.getText()));
//        assertThat(comment.getItem().getId(), equalTo(item2.getId()));
//        assertThat(comment.getAuthor().getId(), equalTo(user1.getId()));
//        assertThat(comment.getCreated(), notNullValue());
//    }
//
//    @Test
//    @DisplayName("Добавляем comment для item, которую не брали в аренду")
//    void saveCommentWithoutUse() {
//        CommentCreateDto commentDto = CommentCreateDto.builder().text("SomeText").build();
//        assertThatThrownBy(() -> commentService.saveComment(user3.getId(), item2.getId(), commentDto))
//                .isInstanceOf(NotAvailableException.class)
//                .hasMessageContaining("Not had bookings user id: " + user3.getId() + " for item id " + item2.getId());
//
//    }
//
//    @Test
//    @DisplayName("Добавляем comment владельцем item")
//    void saveCommentOwnerItem() {
//        CommentCreateDto commentDto = CommentCreateDto.builder().text("SomeText").build();
//        assertThatThrownBy(() -> commentService.saveComment(user2.getId(), item2.getId(), commentDto))
//                .isInstanceOf(CommentNotAllowedException.class)
//                .hasMessageContaining("User id: " + user2.getId() + " is owner  item id: " + item2.getId());
//
//    }
//}