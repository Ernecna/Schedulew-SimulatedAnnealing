import java.io.*;
import java.util.*;

public class Main {

    public static List<Class> readCsv(String filePath) {
        List<Class> classes = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split(",");
                // CSV dosyasının ilk satırı başlık satırıysa, bu satırı atlamak için bir kontrol ekleyebilirsiniz
                String className = values[0];
                int capacity = Integer.parseInt(values[1]);
                classes.add(new Class(className, capacity));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static void readCsv2(String filePath, HashMap<String, Student> students, HashMap<String, Professor> professors, List<Course> courses) {
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split(",");
                // Eğer sütun sayısı beklenenden azsa, bu satırı atla
                if (values.length < 4) {
                    continue;
                }
                String studentID = values[0].trim();
                String profName = values[1].trim();
                String courseID = values[2].trim();
                int duration = Integer.parseInt(values[3].trim());


                Course course = new Course(duration, courseID);
                if(!courses.contains(course)){
                    courses.add(course);
                }


                Student student = students.getOrDefault(studentID, new Student(studentID, new ArrayList<>()));
                student.getCourses().add(course);
                students.put(studentID, student);



                Professor professor = professors.getOrDefault(profName, new Professor(profName, new ArrayList<>()));
                professor.getCourses().add(course);
                professors.put(profName, professor);

            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static List<Course> removeDuplicateCourses(List<Course> courseList) {
        Set<Course> courseSet = new HashSet<>(courseList);
        return new ArrayList<>(courseSet);
    }


    public static void main(String[] args) {
        // Class'ları ve kapasitelerini okuyup bir listeye atıyoruz;
        String filePath = "Classes.csv";
        List<Class> classes = readCsv(filePath);
        //System.out.println(classes.get(0).getClassName()+" "+ classes.get(0).getCapacity());

        ///////////////////////////////////////////////////////////////////////////

        String filePath2 = "1000student.csv"; // CSV dosyasının yolu
        HashMap<String, Student> students = new HashMap<>();
        HashMap<String, Professor> professors = new HashMap<>();
        List<Course> courses = new ArrayList<>();
        readCsv2(filePath2, students, professors, courses);


        /*
        System.out.println(students.get("1500").getStudentID());
        System.out.println(professors.get("Mustafa Yeniad").getProfName());
        System.out.println(courses.get(0).getCourseID());
        System.out.println(courses.get(0).getDuration());
        System.out.println(classes.get(0).getClassName()); */

        ///////////////////////////////////////////////////////////////////////////

        // Tüm profesörler için tekrar eden kursları kaldır ve kurs listelerini güncelle
        for (Map.Entry<String, Professor> entry : professors.entrySet()) {
            String professorName = entry.getKey();
            Professor professor = entry.getValue();

            List<Course> newCourses = removeDuplicateCourses(professor.getCourses());
            professor.setCourses(newCourses);

            // Güncellenmiş kurs listesini göster (opsiyonel)
            //System.out.println("Profesör: " + professorName);
            //professor.displayCourses();
            //System.out.println(); // Satır boşluğu
        }
        //students.get("1500").displayCourses();

        // classes : tüm sınıfların bulunduğu class (obje) listesi
        // students : tüm students'lerin bulunduğu class (obje) HashMap listesi
        // professors : tüm professors bulunduğu class (obje) HashMap listesi
        // coursess : tüm kursların bulunduğu class (obje) listesi


        /*for(Course course : courses){
            System.out.println(course.getCourseID());
        } */

        // kurslar arasında gez
        for (Map.Entry<String, Student> entry : students.entrySet()){
            for(Course course : entry.getValue().getCourses()){
                for(Course course1 : courses){
                    if (course.getCourseID().equals(course1.getCourseID())){
                        course1.addStudent(entry.getValue());
                    }
                }
            }
        }

        // kursu alan studentler
        for(Course course : courses){
            System.out.println("\n\n"+course.getCourseID()+" ;");
            course.displayStudents();
        }















    }
}