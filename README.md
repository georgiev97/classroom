Classroom Management API
This API allows you to manage courses, students, and teachers within a classroom setting. It includes functionalities for creating and updating courses, enrolling students and teachers, and generating various reports.

Resources

Course Resource

Create a Course
POST: http://localhost:8080/api/v1/courses
{
    "courseName": "Science",
    "courseTypeName": "Main"
}

Get a Specific Course
GET: http://localhost:8080/api/v1/courses/course/Mathematics

Get All Courses
GET: http://localhost:8080/api/v1/courses/

Student Resource

Create a Student
POST: http://localhost:8080/api/v1/students
{
    "studentName": "Georgi",
    "studentAge": "24",
    "studentGroupName": "A1"
}

Update a Student
PUT: http://localhost:8080/api/v1/students/{ID}
{
    "studentName": "Georgi",
    "studentAge": "25",
    "studentGroupName": "A1"
}

Enroll a Student in a Course
POST: http://localhost:8080/api/v1/students/enroll
{
    "studentId": STUDENT-ID,
    "courseName": "Mathematics",
    "courseType": "Main"
}

Remove a Student from a Course
POST: http://localhost:8080/api/v1/students/leave
{
    "studentId": TUDENT-ID,
    "courseName": "Mathematics"
}

Teacher Resource

Create a Teacher
POST: http://localhost:8080/api/v1/teachers
{
    "teacherName": "Rumyana",
    "teacherAge": "29",
    "teacherGroupName": "A1"
}

Update a Teacher
PUT: http://localhost:8080/api/v1/teachers/{ID}
{
    "teacherName": "Rumyana",
    "teacherAge": "28",
    "teacherGroupName": "A1"
}

Enroll a Teacher in a Course
POST: http://localhost:8080/api/v1/teachers/enroll
{
    "teacherId": TEACHER-ID,
    "courseName": "Mathematics",
    "courseType": "Main"
}

Remove a Teacher from a Course
POST: http://localhost:8080/api/v1/teachers/leave
{
    "teacherId": TEACHER-ID,
    "courseName": "Mathematics"
}

Report Resource

Get Teacher Count
GET: http://localhost:8080/api/v1/reports/teachers/count

Get Student Count
GET: http://localhost:8080/api/v1/reports/students/count

Get Main Course Count
GET: http://localhost:8080/api/v1/reports/courses/count/main

Get Students in Group
GET: http://localhost:8080/api/v1/reports/students/group/A1

Get Course Students in Group
GET: http://localhost:8080/api/v1/reports/course/Mathematics/group/A1

Get Students in Course by Age
GET: http://localhost:8080/api/v1/reports/students/course/Mathematics/age/21

