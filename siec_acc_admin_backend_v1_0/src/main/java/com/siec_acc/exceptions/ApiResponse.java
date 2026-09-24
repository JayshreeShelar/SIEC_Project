package com.siec_acc.exceptions;


import lombok.*;
import java.time.LocalDateTime;


@Builder
public class ApiResponse<T> {
    private String status;      // SUCCESS / ERROR
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse(){}

    public ApiResponse(String status, String message, T data, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("SUCCESS").message(message).data(data)
                .timestamp(LocalDateTime.now()).build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status("ERROR").message(message).data(null)
                .timestamp(LocalDateTime.now()).build();
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}