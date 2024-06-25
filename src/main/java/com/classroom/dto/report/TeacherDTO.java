package com.classroom.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDTO {

    private String teacherId;
    private String teacherName;
    private int teacherAge;
    private String teacherGroupName;
    private Set<String> courseNames;
}
