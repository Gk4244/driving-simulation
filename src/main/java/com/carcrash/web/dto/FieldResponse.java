package com.carcrash.web.dto;

import com.carcrash.model.Field;

public record FieldResponse(int width, int height) {
    public static FieldResponse from(Field field) {
        return new FieldResponse(field.width(), field.height());
    }
}
