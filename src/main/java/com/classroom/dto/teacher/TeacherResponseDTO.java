package com.classroom.dto.teacher;

import com.classroom.dto.course.CourseResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherResponseDTO {

    private String teacherId;
    private String teacherName;
    private int teacherAge;
    private String teacherGroupName;
    private Set<CourseResponseDTO> teacherCourses;
}
