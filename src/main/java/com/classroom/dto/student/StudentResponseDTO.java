package com.classroom.dto.student;

import com.classroom.dto.course.CourseResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDTO {

    private String id;
    private String studentName;
    private int age;
    private String group;
    private List<CourseResponseDTO> studentCourses;
}
