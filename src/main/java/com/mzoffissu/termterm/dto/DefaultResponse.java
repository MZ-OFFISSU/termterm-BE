package com.mzoffissu.termterm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DefaultResponse<T> {
    private Integer status;
    private String message;
    private T data;

    public DefaultResponse(final Integer status, final String message){
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public static<T> DefaultResponse<T> create(final Integer status, final String message){
        return create(status, message, null);
    }

    public static<T> DefaultResponse<T> create(final Integer status, final String message, final T t){
        return DefaultResponse.<T>builder()
                .status(status)
                .message(message)
                .data(t)
                .build();
    }
}
