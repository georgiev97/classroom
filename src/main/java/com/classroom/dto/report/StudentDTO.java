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
public class StudentDTO {

    private String studentId;
    private String studentName;
    private int studentAge;
    private String studentGroupName;
    private Set<String> courseNames;
}
