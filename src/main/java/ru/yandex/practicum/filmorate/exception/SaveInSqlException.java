package ru.yandex.practicum.filmorate.exception;

public class SaveInSqlException extends RuntimeException {
    public SaveInSqlException(String message) {
        super(message);
    }
}
