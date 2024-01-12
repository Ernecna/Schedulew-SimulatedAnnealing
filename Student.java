import java.util.List;

public class Student {

    private String studentID;
    private List<Course> course;

    public Student(String studentID, List<Course> course){
        this.studentID = studentID;
        this.course = course;
    }



    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public List<Course> getCourses() {
        return course;
    }

    public void setCourses(List<Course> courses) {
        this.course = courses;
    }


    public void displayCourses(){
        for (Course course : course){
            System.out.println(course.getCourseID());
        }
    }
}
