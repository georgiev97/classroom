package com.classroom.dto.teacher;

import com.classroom.enumartion.CourseType;

import java.util.UUID;

public class EnrollTeacherRequestDTO {

    private UUID teacherId;

    private String courseName;
    private CourseType courseType;
}
