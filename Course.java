import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Course {
    private int Duration;
    private String courseID;

    private List<Student> students = new ArrayList<>();

    public void addStudent(Student student){
        students.add(student);
    }
    public Course(int Duration, String courseID){
        this.courseID = courseID;
        this.Duration = Duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(courseID, course.courseID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseID);
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int duration) {
        Duration = duration;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }


    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void displayStudents(){
        for (Student student : students){
            System.out.print(student.getStudentID()+" , ");

        }
    }
}
