package ru.practicum.shareit.booking.dto;

public enum State {
    ALL, //«все», значение по умолчанию
    CURRENT, //«текущие»
    PAST, //«завершённые»
    FUTURE, //«будущие»
    WAITING, //«ожидающие подтверждения»
    REJECTED //«отклонённые»
}
