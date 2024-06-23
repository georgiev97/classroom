package com.classroom.dto.student;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateStudentRequestDTO {

    @NotBlank(message = "Name cannot be empty")
    private String studentName;

    private int studentAge;

    @NotBlank(message = "Group cannot be empty")
    private String studentGroupName;
}
