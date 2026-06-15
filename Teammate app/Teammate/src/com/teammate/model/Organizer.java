package com.teammate.model;

public class Organizer extends User {

    public Organizer(String id, String name) {
        super(id, name);
    }

    @Override
    public String getMenuHeader() {
        return "=== ORGANIZER DASHBOARD ===";
    }

    @Override
    public UserType getUserType() {
        return UserType.ORGANIZER;
    }
}