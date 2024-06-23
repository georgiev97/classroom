package com.classroom.dto.course;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCourseRequestDTO {

    @NotBlank(message = "Course name cannot be empty")
    private String courseName;

    @NotBlank(message = "Course type cannot be empty")
    private String courseTypeName;
}
