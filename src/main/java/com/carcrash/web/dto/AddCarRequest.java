package com.carcrash.web.dto;

public record AddCarRequest(String name, int x, int y, String direction, String commands) {
}
