import java.util.List;
import java.util.Objects;

public class Professor {
    private String profName;
    private List<Course> courses;

    public Professor(String profName, List<Course> courses){
        this.profName = profName;
        this.courses = courses;
    }


    public String getProfName() {
        return profName;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public void addCourse(Course course){
        courses.add(course);
    }

    public void displayCourses(){
        for (Course course : courses){
            System.out.println(course.getCourseID());
        }
    }

}
