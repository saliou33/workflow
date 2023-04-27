package com.innov.workflow.core.domain;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

@NoArgsConstructor
public class ApiResponse extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public static final String CODE_TAG = "code";

    public static final String MSG_TAG = "msg";

    public static final String DATA_TAG = "data";


    public ApiResponse(HttpStatus code, String msg) {
        super.put(CODE_TAG, code.name());
        super.put(MSG_TAG, msg);
    }

    public ApiResponse(HttpStatus code, String msg, Object data) {
        super.put(CODE_TAG, code.name());
        super.put(MSG_TAG, msg);

        if (data != null) {
            super.put(DATA_TAG, data);
        }

    }

    public ApiResponse put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public ResponseEntity build() {
        return new ResponseEntity(this, HttpStatus.valueOf((String) this.get(CODE_TAG)));
    }

    public static ResponseEntity created(String msg, Object data) {
        return (new ApiResponse(HttpStatus.CREATED, msg, data)).build();
    }

    public static ResponseEntity created(String msg) {
        return (new ApiResponse(HttpStatus.CREATED, msg)).build();
    }

    public static ResponseEntity success(String msg) {
        return (new ApiResponse(HttpStatus.OK, msg)).build();
    }

    public static ResponseEntity success(String msg, Object data) {
        return (new ApiResponse(HttpStatus.OK, msg, data)).build();
    }

    public static ResponseEntity error(String msg) {
        return (new ApiResponse(HttpStatus.BAD_REQUEST, msg)).build();
    }

    public static ResponseEntity error(String msg, Object data) {
        return (new ApiResponse(HttpStatus.BAD_REQUEST, msg, data)).build();
    }


}
