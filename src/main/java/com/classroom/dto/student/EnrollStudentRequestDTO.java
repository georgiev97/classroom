package com.classroom.dto.student;

import com.classroom.enumartion.CourseType;

import java.util.UUID;

public class EnrollStudentRequestDTO {

    private UUID studentId;

    private String courseName;
    private CourseType courseType;

}
