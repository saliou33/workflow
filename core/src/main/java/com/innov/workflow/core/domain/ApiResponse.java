package com.innov.workflow.core.domain;

import lombok.NoArgsConstructor;

import java.util.HashMap;

@NoArgsConstructor
public class ApiResponse extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public static final String CODE_TAG = "code";

    public static final String MSG_TAG = "msg";

    public static final String DATA_TAG = "data";


    public static final String DEFAULT_SUCCESS_MSG = "Opération Réussie Avec Succés";

    public static final String DEFAULT_CLIENT_ERROR_MSG = "Erreur Client";

    public static final String DEFAULT_SERVER_ERROR_MSG = "Erreur Serveur";

    public enum Type {
        SUCCESS(200),
        CLIENT_ERROR(400),
        SERVER_ERROR(500);

        private final int value;

        Type(int value)      {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    public ApiResponse(Type type, String msg) {
        super.put(CODE_TAG, type.value);
        super.put(MSG_TAG, msg);
    }

    public ApiResponse(Type type, String msg, Object data) {
        super.put(CODE_TAG, type.value);
        super.put(MSG_TAG, msg);

        if (data != null) {
            super.put(DATA_TAG, data);
        }

    }

    public ApiResponse put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public static ApiResponse success(String msg, Object data) {
        return new ApiResponse(Type.SUCCESS, msg, data);
    }

    public static ApiResponse success(String msg) {
        return ApiResponse.success(msg, null);
    }

    public static ApiResponse success(Object data) {
        return ApiResponse.success(DEFAULT_SUCCESS_MSG, data);
    }

    public static ApiResponse success() {
        return ApiResponse.success(DEFAULT_SUCCESS_MSG);
    }


    public static ApiResponse clientError(String msg, Object data) {
        return new ApiResponse(Type.CLIENT_ERROR, msg, data);
    }

    public static ApiResponse clientError(String msg) {
        return ApiResponse.success(msg, null);
    }

    public static ApiResponse clientError(Object data) {
        return ApiResponse.success(DEFAULT_CLIENT_ERROR_MSG, data);
    }

    public static ApiResponse clientError() {
        return ApiResponse.success(DEFAULT_CLIENT_ERROR_MSG);
    }

    public static ApiResponse serverError(String msg, Object data) {
        return new ApiResponse(Type.SUCCESS, msg, data);
    }

    public static ApiResponse serverError(String msg) {
        return ApiResponse.success(msg, null);
    }

    public static ApiResponse serverError(Object data) {
        return ApiResponse.success(DEFAULT_SERVER_ERROR_MSG, data);
    }

    public static ApiResponse serverError() {
        return ApiResponse.success(DEFAULT_SERVER_ERROR_MSG);
    }


}
