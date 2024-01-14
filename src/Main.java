import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static Map<String, String> blockCourseSchedule = new HashMap<>();
    ////////////////////////////////////////////////////////////////// READ&DİSPLAY CLASSROOM
    private static List<Classroom> readClassroomsFromCSV(String fileName) {
        List<Classroom> classrooms = new ArrayList<>();
        String line = "";
        String splitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader("Classes.csv"))) {
            while ((line = br.readLine()) != null) {
                String[] classroomData = line.split(splitBy);
                Classroom classroom = new Classroom(classroomData[0], Integer.parseInt(classroomData[1]));
                classrooms.add(classroom);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classrooms;
    }
    public static void displayClasses(List<Classroom> classrooms){
        for (Classroom classroom : classrooms) {
            System.out.println("Room ID: " + classroom.getRoomID() + ", Capacity: " + classroom.getCapacity());
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////READ&&DİSPLAY CLASS LİST
    private static List<ClassList> readClassListFromCSV(String fileName) {
        List<ClassList> classLists = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] classData = line.split(",");
                ClassList classList = new ClassList(Integer.parseInt(classData[0].trim()),
                        classData[1].trim(),
                        classData[2].trim(),
                        Integer.parseInt(classData[3].trim()));
                classLists.add(classList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classLists;
    }
    public static void displayClassList(List<ClassList> classlist){
        for (ClassList classList : classlist) {
            System.out.println("Student ID: " + classList.getStudentID() +
                    ", Professor Name: " + classList.getProfessorName() +
                    ", Course ID: " + classList.getCourseID() +
                    ", Exam Duration: " + classList.getExamDuration());
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////CREATE GRAPH BY USİNG EXAM NODE
    private static AllExam createExamGraph(List<ClassList> classLists) {
        AllExam allExam = new AllExam();
        Map<String, Exam> courseExamMap = new HashMap<>();

        // Group students by course ID
        for (ClassList classList : classLists) {
            String courseID = classList.getCourseID();

            if (!courseExamMap.containsKey(courseID)) {
                courseExamMap.put(courseID, new Exam(new ArrayList<>(),
                        classList.getProfessorName(),
                        classList.getCourseID(),
                        classList.getExamDuration()));
            }
            courseExamMap.get(courseID).getStudentIDs().add(classList.getStudentID());
        }

        // Add nodes to the graph
        for (Exam exam : courseExamMap.values()) {
            allExam.addNode(exam);
        }

        return allExam;
    }




    //////////////////////////////////////////  ALGO  //////////////////////////////////////////////////////////////
    private static final int DAYS = 6; // Monday to Saturday
    private static final int HOURS = 9; // 09:00 to 18:00
    private static final String[] DAY_NAMES = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    // A class to represent a list of exams in a single slot
    private static class ExamSlot {
        List<Exam> exams = new ArrayList<>();

        public void addExam(Exam exam) {
            exams.add(exam);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Exam exam : exams) {
                sb.append(exam.getCourseID()).append(" (").append(exam.getProfessorName()).append("), ");
            }
            return !sb.isEmpty() ? sb.substring(0, sb.length() - 2) : "Free";
        }
    }

    // Method to create and initialize the timetable
    private static ExamSlot[][] createTimetable() {
        return new ExamSlot[DAYS][HOURS];
    }

    // Method to schedule exams in the timetable
    private static void scheduleExams(AllExam allExam, ExamSlot[][] timetable) {
        // Schedule block course exams first
        for (Map.Entry<String, String> entry : blockCourseSchedule.entrySet()) {
            String courseId = entry.getKey();
            String[] dayTime = entry.getValue().split("-");
            int day = Arrays.asList(DAY_NAMES).indexOf(dayTime[0]);
            int hour = Integer.parseInt(dayTime[1]) - 9;

            Exam exam = allExam.getNodes().stream()
                    .filter(e -> e.getCourseID().equals(courseId))
                    .findFirst()
                    .orElse(null);

            if (exam != null) {
                if (timetable[day][hour] == null) {
                    timetable[day][hour] = new ExamSlot();
                }
                timetable[day][hour].addExam(exam);
                exam.setScheduled(true); // Mark the block course exam as scheduled
            } else {
                System.out.println("!!! No exam found for course ID: " + courseId);
            }
        }

        Random rand = new Random();
        Map<String, Set<Integer>> professorDaysMap = new HashMap<>();

        // Schedule other exams
        for (Exam exam : allExam.getNodes()) {
            if (exam.isScheduled()) continue; // Skip if the exam is already scheduled (like block courses)

            boolean scheduled = false;
            while (!scheduled) {
                int day = rand.nextInt(DAYS);
                int hour = rand.nextInt(HOURS);

                if (canScheduleExam(timetable, day, hour, exam.getProfessorName(), professorDaysMap)) {
                    if (timetable[day][hour] == null) {
                        timetable[day][hour] = new ExamSlot();
                    }
                    timetable[day][hour].addExam(exam);
                    scheduled = true;
                    exam.setScheduled(true); // Mark the exam as scheduled
                    professorDaysMap.computeIfAbsent(exam.getProfessorName(), k -> new HashSet<>()).add(day);
                }
            }
        }
    }


    // Method to check if an exam can be scheduled at the given day and hour
    private static boolean canScheduleExam(ExamSlot[][] timetable, int day, int hour, String professorName, Map<String, Set<Integer>> professorDaysMap) {
        if (timetable[day][hour] != null) {
            for (Exam exam : timetable[day][hour].exams) {
                if (exam.getProfessorName().equals(professorName)) {
                    return false; // Professor already has an exam at this slot
                }
            }
        }

        // Check if the professor already has an exam on this day
        return !professorDaysMap.getOrDefault(professorName, Collections.emptySet()).contains(day);
    }

    private static void printTimetable(ExamSlot[][] timetable) {
        for (int day = 0; day < DAYS; day++) {
            System.out.println(DAY_NAMES[day] + ":");
            for (int hour = 0; hour < HOURS; hour++) {
                // This assumes that the timetable starts at 9 AM
                int startHour = (hour + 9); // Start hours in 24-hour format
                String timeSuffix = startHour >= 12 && startHour < 24 ? "PM" : "AM";
                if (startHour > 12) {
                    startHour -= 12; // Convert to 12-hour format for PM times
                }
                if (startHour == 0) {
                    startHour = 12; // Adjust for midnight
                }

                // Loop through each slot in the timetable
                if (timetable[day][hour] != null && !timetable[day][hour].exams.isEmpty()) {
                    for (Exam exam : timetable[day][hour].exams) {
                        int examDurationHours = exam.getExamDuration() / 60;
                        int examDurationMinutes = exam.getExamDuration() % 60;
                        int endHour24 = (hour + 9 + examDurationHours); // End hour in 24-hour format
                        String endTimeSuffix = endHour24 >= 12 && endHour24 < 24 ? "PM" : "AM";
                        if (endHour24 >= 24) {
                            endHour24 -= 24; // Adjust for times past midnight
                        }
                        int endHour = endHour24 > 12 ? endHour24 - 12 : endHour24;
                        if (endHour == 0) {
                            endHour = 12; // Adjust for midnight
                        }
                        if (endHour24 == 12) { // Handle noon
                            endTimeSuffix = "PM";
                        }
                        if (endHour24 == 24) { // Handle midnight
                            endTimeSuffix = "AM";
                        }

                        // Check for block courses
                        boolean isBlockCourse = blockCourseSchedule.containsKey(exam.getCourseID());

                        // For block course, append "(Block)" to the course ID
                        String courseDisplay = isBlockCourse ? exam.getCourseID() + " (Common Course)" : exam.getCourseID();

                        // Find the room assignment
                        String roomAssignment = exam.getAssignedClassrooms().stream()
                                .map(Classroom::getRoomID)
                                .collect(Collectors.joining(", "));

                        // Print the time, course,and room information
                        System.out.printf("%02d:00 %s - %02d:%02d %s: %s - Room %s\n",
                        startHour, timeSuffix, endHour, examDurationMinutes, endTimeSuffix, courseDisplay, roomAssignment);
                    }
                }
            }
                        System.out.println();
        }
    }







    /////////////////////////////////  CALCULATE FAULT SCORE  /////////////////////////////////////////////
    private static String createTimeSlotKey(int day, int hour) {
        return day + "-" + hour;
    }

    private static int calculateFaultScore(ExamSlot[][] timetable) {
        Map<Integer, Map<String, Integer>> studentExamTimings = new HashMap<>();
        int faultScore = 0;

        // Populate the map with exam timings for each student
        for (int day = 0; day < DAYS; day++) {
            for (int hour = 0; hour < HOURS; hour++) {
                if (timetable[day][hour] != null && !timetable[day][hour].exams.isEmpty()) {
                    String timeSlotKey = createTimeSlotKey(day, hour);
                    for (Exam exam : timetable[day][hour].exams) {
                        for (Integer studentID : exam.getStudentIDs()) {
                            studentExamTimings.computeIfAbsent(studentID, k -> new HashMap<>())
                                    .merge(timeSlotKey, 1, Integer::sum);
                        }
                    }
                }
            }
        }

        // Count conflicts for each student in each time slot
        for (Map<String, Integer> timings : studentExamTimings.values()) {
            for (int count : timings.values()) {
                if (count > 1) {
                    faultScore += count - 1; // For each overlapping exam in a time slot
                }
            }
        }

        return faultScore;
    }


    ///////////////////////////////////////////  SIMULATED ANNEALING  //////////////////////////////7
    private static boolean simulatedAnnealing(ExamSlot[][] timetable) {
        Random rand = new Random();
        int currentFaultScore = calculateFaultScore(timetable);
        int iteration = 0;

        while (currentFaultScore > 0) {
            iteration++;

            int day = rand.nextInt(DAYS);
            int hour = rand.nextInt(HOURS);
            if (timetable[day][hour] == null || timetable[day][hour].exams.isEmpty()) {
                continue;
            }
            Exam selectedExam = selectRandomExam(timetable[day][hour]);

            if (selectedExam != null) {
                int newDay = rand.nextInt(DAYS);
                int newHour = rand.nextInt(HOURS);

                if (!isValidMove(timetable, newDay, selectedExam)) {
                    continue;
                }

                moveExam(timetable, day, hour, newDay, newHour, selectedExam);
                int newFaultScore = calculateFaultScore(timetable);

                if (newFaultScore < currentFaultScore) {
                    currentFaultScore = newFaultScore;
                } else {
                    moveExam(timetable, newDay, newHour, day, hour, selectedExam);
                }
            }
        }

        return currentFaultScore == 0;
    }



    private static Exam selectRandomExam(ExamSlot examSlot) {
        Random rand = new Random();
        List<Exam> movableExams = examSlot.exams.stream()
                .filter(exam -> !blockCourseSchedule.containsKey(exam.getCourseID()))
                .collect(Collectors.toList());

        if (movableExams.isEmpty()) {
            return null; // No movable exam in this slot
        }

        int examIndex = rand.nextInt(movableExams.size());
        return movableExams.get(examIndex);
    }




    private static boolean isValidMove(ExamSlot[][] timetable, int newDay, Exam exam) {
        String professorName = exam.getProfessorName();
        // Check all slots on the new day
        for (int hour = 0; hour < HOURS; hour++) {
            if (timetable[newDay][hour] != null) {
                for (Exam scheduledExam : timetable[newDay][hour].exams) {
                    if (scheduledExam.getProfessorName().equals(professorName)) {
                        return false; // Professor has an exam on the new day
                    }
                }
            }
        }
        return true; // No conflict found
    }


    private static void moveExam(ExamSlot[][] timetable, int fromDay, int fromHour, int toDay, int toHour, Exam exam) {
        // Remove exam from current slot
        if (timetable[fromDay][fromHour] != null) {
            timetable[fromDay][fromHour].exams.remove(exam);
        }

        // Add exam to new slot
        if (timetable[toDay][toHour] == null) {
            timetable[toDay][toHour] = new ExamSlot();
        }
        timetable[toDay][toHour].addExam(exam);
    }

    ////////////////////////////////////////////  ASSIGN CLASSROOMS  /////////////////////////////////////////////////
    // Method to assign classrooms to exams
    private static void assignClassroomsToExams(AllExam allExam, List<Classroom> classrooms, ExamSlot[][] timetable) {
        Random rand = new Random();
        Map<String, Boolean> classroomUsageMap = new HashMap<>(); // To track daily classroom allocation

        for (int day = 0; day < DAYS; day++) {
            // Reset classroom usage for each day
            classroomUsageMap.clear();

            for (int hour = 0; hour < HOURS; hour++) {
                if (timetable[day][hour] != null && !timetable[day][hour].exams.isEmpty()) {
                    for (Exam exam : timetable[day][hour].exams) {
                        allocateClassroom(exam, classrooms, classroomUsageMap, rand);
                    }
                }
            }
        }
    }

    // Helper method to allocate classroom to an exam
    private static void allocateClassroom(Exam exam, List<Classroom> classrooms, Map<String, Boolean> classroomUsageMap, Random rand) {
        int numberOfStudents = exam.getNumberOfStudents();
        int requiredCapacity = numberOfStudents; // Full capacity needed as each class can hold half its stated capacity
        List<Classroom> assignedClassrooms = new ArrayList<>();

        while (requiredCapacity > 0) {
            int randomIndex = rand.nextInt(classrooms.size());
            Classroom classroom = classrooms.get(randomIndex);

            // Check if classroom is already used for the day and if it has enough remaining capacity
            if (!classroomUsageMap.getOrDefault(classroom.getRoomID(), false) && (classroom.getCapacity() / 2) >= requiredCapacity) {
                assignedClassrooms.add(classroom);
                requiredCapacity = 0; // All students accommodated
                classroomUsageMap.put(classroom.getRoomID(), true); // Mark classroom as used for the day
            } else if (!classroomUsageMap.getOrDefault(classroom.getRoomID(), false)) {
                // Partially fill the classroom
                assignedClassrooms.add(classroom);
                requiredCapacity -= classroom.getCapacity() / 2; // Utilize half capacity
                classroomUsageMap.put(classroom.getRoomID(), true); // Mark classroom as used for the day
            }
        }

        exam.setAssignedClassrooms(assignedClassrooms);
    }

    // Method to read block course schedule from user input
    private static void readBlockCourseSchedule(List<ClassList> classLists) {
        Scanner scanner = new Scanner(System.in);

        // Print out each course ID only once for reference
        Set<String> uniqueCourseIds = classLists.stream()
                .map(ClassList::getCourseID)
                .collect(Collectors.toSet());



        System.out.print("Enter the number of block courses: ");
        int numberOfBlockCourses = scanner.nextInt();
        scanner.nextLine(); // consume the leftover newline

        for (int i = 0; i < numberOfBlockCourses; i++) {
            String courseId="";
            String day;
            int hour;

            System.out.println("Enter the details for block course " + (i + 1) + ":");
            System.out.print("Available Course IDs: [");
            for (String courseIds : uniqueCourseIds) {
                System.out.print(courseIds+", ");
            }
            System.out.println("]");

            do {
                System.out.print("Enter the course ID: ");
                courseId = scanner.nextLine();
                if (!uniqueCourseIds.contains(courseId)){
                    System.out.println("Invalid Course ID. Please enter a valid Course ID from the list above.");
                }
            } while(!uniqueCourseIds.contains(courseId));

            do {
                System.out.print("Enter the day (Monday, Tuesday, Wednesday, Thursday, Friday, Saturday): ");
                day = scanner.nextLine();

                if (!Arrays.asList(DAY_NAMES).contains(day)) {
                    System.out.println("Invalid day. Please enter a valid day of the week (e.g., Monday, Tuesday, etc.).");
                }
            } while (!Arrays.asList(DAY_NAMES).contains(day));
            do {
                System.out.print("Enter the hour (9-18): ");
                while (!scanner.hasNextInt()) {
                    System.out.print("That's not a number! Enter the hour (9-18): ");
                    scanner.next(); // consume the non-integer input
                }
                hour = scanner.nextInt();
                scanner.nextLine(); // consume the newline after the number
                if (!(hour >= 9 && hour <= 17)) {
                    System.out.println("Invalid hour. Please enter a valid hour between 9 and 18.");
                }
            } while (!(hour >= 9 && hour <= 17));

            blockCourseSchedule.put(courseId, day + "-" + hour);
        }
    }




    /////////////////  IF 6 DAYS ARE NOT ENOUGH, ADD +1 DAY
    private static ExamSlot[][] extendTimetableForSunday(ExamSlot[][] oldTimetable) {
        ExamSlot[][] newTimetable = new ExamSlot[7][HOURS]; // 7 day, include Sunday
        for (int day = 0; day < 6; day++) {
            System.arraycopy(oldTimetable[day], 0, newTimetable[day], 0, HOURS);
        }
        newTimetable[6] = new ExamSlot[HOURS];
        for (int hour = 0; hour < HOURS; hour++) {
            newTimetable[6][hour] = new ExamSlot();
        }
        return newTimetable;
    }

    ////////////////  ADD COURSES AGAIN AFTER ADDING +1 DAY TO SCHEDULE
    private static void rescheduleRemainingExams(AllExam allExam, ExamSlot[][] timetable) {
        for (Exam exam : allExam.getNodes()) {
            if (!exam.isScheduled()) {
                // Find a convenient time slot for the unscheduled exam
                for (int hour = 0; hour < HOURS; hour++) {
                    if (timetable[6][hour].exams.isEmpty()) { // Check it out for Sunday.
                        timetable[6][hour].addExam(exam);
                        exam.setScheduled(true);
                        break;
                    }
                }
            }
        }
    }




    public static void main(String[] args) {
        List<Classroom> classrooms = readClassroomsFromCSV("Classes.csv");
        //displayClasses(classrooms);
        List<ClassList> classLists = readClassListFromCSV("1000student.csv");
        //displayClassList(classLists);

        readBlockCourseSchedule(classLists);

        //GRAPH READY

        AllExam allExam =createExamGraph(classLists);
        allExam.printGraph();
        System.out.println(allExam.getNumberOfNodes());

        /////////////////////////////////////////////////////////////////////////////////

        ExamSlot[][] timetable = createTimetable();
        scheduleExams(allExam, timetable);

        /////////////////////////////////////  CHECK FOR +1 (SUNDAY) DAY  ///////////////////////////////////////////
        // Check if all exams are scheduled
        boolean allExamsScheduled = true;
        for (Exam exam : allExam.getNodes()) {
            if (!exam.isScheduled()) {
                allExamsScheduled = false;
                break;
            }
        }

        // If not all exams are scheduled, extend the timetable to include Sunday
        if (!allExamsScheduled) {
            timetable = extendTimetableForSunday(timetable);
            // Reschedule remaining exams
            rescheduleRemainingExams(allExam, timetable);
        }
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        int initialFaultScore = calculateFaultScore(timetable);
        System.out.println("Initial Fault Score: " + initialFaultScore);
        printTimetable(timetable);

        // Simulated Annealing to resolve conflicts
        boolean isResolved = simulatedAnnealing(timetable);

        // Print the result
        System.out.println("\n\nConflict Resolution " + (isResolved ? "Successful" + "\n+++++++++++++++++++++++++++++++++++++++++++++" : "Failed" + "\n+++++++++++++++++++++++++++++++++++++++"));
        //System.out.println("Final Fault Score: " + finalFaultScore);

        assignClassroomsToExams(allExam, classrooms, timetable);

        // Print the final timetable
        printTimetable(timetable);


        // After scheduling exams
        int faultScore = calculateFaultScore(timetable);
        System.out.println("Total Fault Score (Student Conflicts): " + faultScore);




    }

}
