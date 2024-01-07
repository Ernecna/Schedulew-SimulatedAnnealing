import java.util.ArrayList;
import java.util.List;

public class AllExam {

        private List<Exam> nodes;

        public AllExam() {
            nodes = new ArrayList<>();
        }

        public void addNode(Exam node) {
            nodes.add(node);
        }

        // Method to add an edge (conflict) between two nodes
        public void addConflict(Exam node1, Exam node2) {
            node1.addConflict(node2);
            node2.addConflict(node1);
        }

        public List<Exam> getNodes() {
            return nodes;
        }

    public int getNumberOfNodes() {
        return nodes.size();
    }
    public void printGraph() {
        for (Exam node : nodes) {
            System.out.println("Course ID: " + node.getCourseID() + ", Professor: " + node.getProfessorName()
                    + ", Exam Duration: " + node.getExamDuration() + ", Student IDs: " + node.getStudentIDs());
            System.out.print("Number of students: "+node.getNumberOfStudents());
            System.out.println("\n");
        }
    }
}
