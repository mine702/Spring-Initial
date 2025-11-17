package com.example.Initial.entity.enums;

public enum OperationLevelType {
    USER,
    ADMIN;

    public static OperationLevelType fromApiName(String apiName) {
        if (apiName != null) {
            String lower = apiName.toLowerCase();
            if (lower.contains("user")) return USER;
        }
        return ADMIN;
    }
}
