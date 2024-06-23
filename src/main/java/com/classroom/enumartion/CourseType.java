package com.classroom.enumartion;

public enum CourseType {
    MAIN("main"),
    SECONDARY("secondary");

    private final String value;

    CourseType(String value) {
        this.value = value;
    }

    public static CourseType fromString(String value) {
        for (CourseType courseType : CourseType.values()) {
            if (courseType.value.equalsIgnoreCase(value)) {
                return courseType;
            }
        }
        throw new IllegalArgumentException("No enum constant with name " + value);
    }
}
