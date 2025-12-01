package ru.practicum.shareit.item.comment.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class CommentMapper {
    public static Comment mapToComment (CommentCreateDto commentCreateDto, User user, Item item) {
        log.debug("Начало конвертации объекта CommentCreateDto в объект класса Comment");
        Comment comment = new Comment();
        comment.setText(commentCreateDto.getText());
        comment.setAuthor(user);
        comment.setItem(item);
        log.debug("Окончание конвертации запроса в объект класса Comment");
        return comment;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        log.debug("Начало конвертации объекта Comment в объект класса CommentDto");
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        log.debug("Окончание конвертации объекта Comment в объект класса CommentDto");
        return commentDto;
    }
}
