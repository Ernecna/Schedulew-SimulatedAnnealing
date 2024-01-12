import java.util.ArrayList;
import java.util.List;

public class Exam {

        private List<Integer> studentIDs; // List of student IDs
        private String professorName;
        private String courseID;
        private int examDuration;
        private List<Exam> conflicts;
        private boolean isScheduled = false;
    private List<Classroom> assignedClassrooms; // New field to store assigned classrooms

        public Exam(List<Integer> studentIDs, String professorName, String courseID, int examDuration) {
            this.studentIDs = studentIDs;
            this.professorName = professorName;
            this.courseID = courseID;
            this.examDuration = examDuration;
            this.conflicts = new ArrayList<>();
            this.assignedClassrooms = new ArrayList<Classroom>();
        }

        // Methods to add and get conflicts
        public void addConflict(Exam node) {
            conflicts.add(node);
        }

        public List<Exam> getConflicts() {
            return conflicts;
        }

        // Getter methods for the properties
        public List<Integer> getStudentIDs() {
            return studentIDs;
        }

        public String getProfessorName() {
            return professorName;
        }

        public String getCourseID() {
            return courseID;
        }

        public int getExamDuration() {
            return examDuration;
        }

        public int getNumberOfStudents() {
        return studentIDs.size();
    }

    public void setAssignedClassrooms(List<Classroom> classrooms) {
        this.assignedClassrooms = classrooms;
    }

    // Method to get the assigned classrooms
    public List<Classroom> getAssignedClassrooms() {
        return assignedClassrooms;
    }

    public int calculateRequiredClassrooms(int classroomCapacity) {
        return (int) Math.ceil((double) getNumberOfStudents() / (classroomCapacity / 2));
    }

    // Method to mark this exam as scheduled
    public void setScheduled(boolean scheduled) {
        this.isScheduled = scheduled;
    }

    public boolean isScheduled() {
        return this.isScheduled;
    }

}
