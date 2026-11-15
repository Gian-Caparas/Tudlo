package com.csit321G6.groufings.tudlo.entity;

public enum UserType {
    ADMIN,
    DEVELOPER,
    TEACHER,
    STUDENT;

    public int rank() {
        return switch (this) {
            case DEVELOPER -> 4;
            case ADMIN -> 3;
            case TEACHER -> 2;
            case STUDENT -> 1;
        };
    }

    public boolean canManage(UserType target) {
        return rank() > target.rank();
    }
}
