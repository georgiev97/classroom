package com.classroom.dto.course;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseResponseDTO {

    private String courseName;
    private String courseTypeName;
}
