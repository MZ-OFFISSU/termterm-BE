package com.mzoffissu.termterm.response;

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

    public static<T> DefaultResponse<T> create(final BaseResponseType response){
        return create(response, null);
    }

    public static<T> DefaultResponse<T> create(final BaseResponseType response, final T t){
        return DefaultResponse.<T>builder()
                .status(response.getCode())
                .message(response.getMessage())
                .data(t)
                .build();
    }
}
