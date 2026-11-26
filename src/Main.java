import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Main{

    static class InvalidScoreException extends Exception {
        public InvalidScoreException(String message) {
            super(message);
        }
    }

    static class InvalidInputException extends Exception {
        public InvalidInputException(String message) {
            super(message);
        }
    }

    static Scanner input = new Scanner(System.in);
    
    static String divider = "----------------------------------------------------------------------------------------";

    static ArrayList<Subject> subjects = new ArrayList<>();
    static StudyTracker tracker = new StudyTracker();
    static Recommendation recommendation = new Recommendation();
    static Student student;

    public static boolean MainMenu() {
        System.out.println(divider);
        System.out.println(" Studify: An Adaptive Study Strategy and Progress Tracking System");
        System.out.println(divider);
        System.out.println("[1] -  Update Profile");
        System.out.println("[2] -  Display All Subjects");
        System.out.println("[3] -  Add/Update Subjects");
        System.out.println("[4] -  Delete Subject/Topic");
        System.out.println("[5] -  Recommend a Topic Priority");
        System.out.println("[6] -  Recommend a Study Strategy");
        System.out.println("[7] -  Study Session Tracker and Progress Reports");
        System.out.println("[8] -  Study Strategies (Flashcards, Practice Exercises, Spaced Repetition)");
        System.out.println("[9] -  Save and Exit");
        System.out.println(divider);
        
        System.out.print("Select an option (1-9): ");
        int nextRoute;
        try {
            nextRoute = Integer.parseInt(input.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Input: please enter a number from 1 to 9.");
            return true;
        }

        
        switch (nextRoute) {
            case 1 -> UpdateProfile();
            case 2 -> DisplayAllSubjects();
            case 3 -> AddNewSubjects();
            case 4 -> DeleteSubject();
            case 5 -> RecommendTopicPriority();
            case 6 -> RecommendStudyStrategy();
            case 7 -> SessionTrackerandProgressReports();
            case 8 -> StudyStrategyTools();
            case 9 -> { SaveData(); askBackToMainMenu(); return false; }
            default -> { System.out.println("Invalid Input"); return true; }
        };
        
        return askBackToMainMenu();
    }

    public static void DisplayAllSubjects () {
        System.out.println(divider);
        System.out.println(" ALL SUBJECTS");
        System.out.println(divider);
        if (subjects.isEmpty()) {
            System.out.println("No subjects yet. Choose option 2 to add one.");
            return;
        }
        for (int i = 0; i < subjects.size(); i++) {
            Subject s = subjects.get(i);
            System.out.println("[" + (i + 1) + "] " + s.getSubjectName() + "  (Assessment: " + InputHelper.formatDate(s.getAssessmentDate()) + ")");
            if (s.getTopicCount() == 0) {
                System.out.println("      No topics yet.");
            }
            for (int j = 0; j < s.getTopicCount(); j++) {
                Topic t = s.getTopic(j);
                System.out.println("      - " + t.getTopicName() + " | Difficulty: " + t.getDifficulty() + "/5 | Quiz: " + t.getScoreText()
                        + " | Confidence: " + t.getConfidenceText() + " | Assessment: " + InputHelper.formatDate(t.getAssessmentDate()));
            }
        }
    }

    public static void AddNewSubjects () {
        System.out.println(divider);
        System.out.println(" ADD NEW SUBJECTS");
        System.out.println(divider);
        System.out.println("[1] -  Add a new subject");
        System.out.println("[2] -  Add a topic to an existing subject");
        System.out.println("[3] -  Update a subject");
        System.out.println("[4] -  Back");
        int choice = InputHelper.readIntInRange(input, "Select an option (1-4): ", 1, 4);

        switch (choice) {
            case 1 -> addSubjectWithTopics();
            case 2 -> {
                Subject s = pickSubject();
                if (s != null) {
                    addTopicTo(s);
                }
            }
            case 3 -> updateSubject();
            default -> { }
        }
    }

    public static void DeleteSubject () {
        System.out.println(divider);
        System.out.println(" DELETE");
        System.out.println(divider);
        System.out.println("[1] -  Delete a subject");
        System.out.println("[2] -  Delete a topic");
        System.out.println("[3] -  Back");
        int choice = InputHelper.readIntInRange(input, "Select an option (1-3): ", 1, 3);

        if (choice == 1) {
            Subject s = pickSubject();
            if (s != null && InputHelper.readYesNo(input, "Delete \"" + s.getSubjectName() + "\" and all its topics? (y/n): ")) {
                tracker.removeSessions(s.getSubjectName(), null);
                subjects.remove(s);
                System.out.println("Subject deleted.");
            }
        } else if (choice == 2) {
            Subject s = pickSubject();
            if (s == null) {
                return;
            }
            Topic t = pickTopic(s);
            if (t == null) {
                return;
            }
            try {
                s.removeTopic(t.getTopicName());
                tracker.removeSessions(s.getSubjectName(), t.getTopicName());
                System.out.println("Topic deleted.");
            } catch (InvalidInputException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void RecommendTopicPriority () {
        System.out.println(divider);
        System.out.println(" TOPIC PRIORITY (highest priority first)");
        System.out.println(divider);
        ArrayList<Topic> ranked = recommendation.prioritizeTopics(subjects);
        if (ranked.isEmpty()) {
            System.out.println("No topics yet. Add a subject with topics first.");
            return;
        }
        for (int i = 0; i < ranked.size(); i++) {
            Topic t = ranked.get(i);
            int score = recommendation.getPriorityScore(t);
            System.out.printf("%2d. %-18s (%s)%n", i + 1, t.getTopicName(), t.getSubjectName());
            System.out.println("      Priority: " + recommendation.getPriorityLabel(score) + " (" + score + "/100) | Quiz: " + t.getScoreText()
                    + " | Confidence: " + t.getConfidenceText() + " | Difficulty: " + t.getDifficulty() + "/5 | Assessment: "
                    + InputHelper.formatDate(t.getAssessmentDate()));
        }
    }

    public static void RecommendStudyStrategy () {
        System.out.println(divider);
        System.out.println(" STUDY STRATEGY RECOMMENDATION");
        System.out.println(divider);
        Subject s = pickSubject();
        if (s == null) {
            return;
        }
        if (s.getTopicCount() == 0) {
            System.out.println("This subject has no topics yet.");
            return;
        }
        for (int i = 0; i < s.getTopicCount(); i++) {
            Topic t = s.getTopic(i);
            StudyStrategy strategy = recommendation.suggestStudyMethod(t);
            System.out.println();
            System.out.println("Topic  : " + t.getTopicName());
            System.out.println("Method : " + strategy.getStrategyName());
            System.out.println("Tip    : " + strategy.recommend(t));
        }
    }

    public static void SessionTrackerandProgressReports () {
        boolean back = false;
        while (!back) {
            System.out.println(divider);
            System.out.println(" STUDY SESSION TRACKER AND PROGRESS REPORTS");
            System.out.println(divider);
            System.out.println("[1] -  Log a study session");
            System.out.println("[2] -  Record a quiz score and confidence level");
            System.out.println("[3] -  View progress report");
            System.out.println("[4] -  Back to main menu");
            int choice = InputHelper.readIntInRange(input, "Select an option (1-4): ", 1, 4);

            switch (choice) {
                case 1 -> logStudySession();
                case 2 -> recordQuiz();
                case 3 -> recommendation.generateReport(subjects, tracker);
                default -> back = true;
            }
        }
    }

    public static void StudyStrategyTools () {
        boolean back = false;
        while (!back) {
            System.out.println(divider);
            System.out.println(" STUDY STRATEGIES");
            System.out.println(divider);
            System.out.println("[1] -  Flashcards (add and review)");
            System.out.println("[2] -  Practice Exercises");
            System.out.println("[3] -  Spaced Repetition");
            System.out.println("[4] -  Back to main menu");
            int choice = InputHelper.readIntInRange(input, "Select an option (1-4): ", 1, 4);

            switch (choice) {
                case 1 -> flashcardMenu();
                case 2 -> practiceMenu();
                case 3 -> spacedRepetitionMenu();
                default -> back = true;
            }
        }
    }

    public static void UpdateProfile () {
        System.out.println(divider);
        System.out.println(" UPDATE PROFILE");
        System.out.println(divider);
        student.viewProfile();
        String name = InputHelper.readOptionalText(input, "New name (press Enter to keep): ");
        String email = InputHelper.readOptionalText(input, "New email (press Enter to keep): ");
        student.updateStudent(name, email);
        System.out.println("Profile updated. Choose option 7 to save it.");
    }

    private static void addSubjectWithTopics() {
        String name = InputHelper.readText(input, "Subject name: ");
        if (findSubject(name) != null) {
            System.out.println("Error: that subject already exists.");
            return;
        }
        LocalDate date = InputHelper.readDate(input, "Subject assessment date (yyyy-MM-dd, or press Enter to skip): ");
        Subject subject = new Subject(nextSubjectId(), name, date);
        subjects.add(subject);
        System.out.println("Subject added.");

        while (!subject.isFull() && InputHelper.readYesNo(input, "Add a topic to " + name + "? (y/n): ")) {
            addTopicTo(subject);
        }
    }

    private static void addTopicTo(Subject subject) {
        String name = InputHelper.readText(input, "Topic name: ");
        int difficulty = InputHelper.readInt(input, "Difficulty (1 = easy, 5 = very hard): ");
        LocalDate date = InputHelper.readDate(input, "Topic assessment date (yyyy-MM-dd, or press Enter to use the subject's date): ");
        if (date == null) {
            date = subject.getAssessmentDate();
        }
        try {
            subject.addTopic(new Topic(name, difficulty, date));
            System.out.println("Topic added.");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateSubject() {
        Subject s = pickSubject();
        if (s == null) {
            return;
        }
        String newName = InputHelper.readOptionalText(input, "New subject name (press Enter to keep \"" + s.getSubjectName() + "\"): ");
        Subject other = newName.isEmpty() ? null : findSubject(newName);
        if (other != null && other != s) {
            System.out.println("Error: another subject already uses that name.");
            return;
        }
        LocalDate newDate = InputHelper.readDate(input, "New assessment date (yyyy-MM-dd, or press Enter to keep it): ");
        String oldName = s.getSubjectName();
        try {
            s.updateSubject(newName, newDate);
            if (!oldName.equals(s.getSubjectName())) {
                renameSessions(oldName, s.getSubjectName());
            }
            System.out.println("Subject updated.");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void renameSessions(String oldName, String newName) {
        ArrayList<StudySession> old = new ArrayList<>(tracker.getSessions());
        tracker.getSessions().clear();
        for (StudySession ss : old) {
            String subjectName = ss.getSubjectName().equalsIgnoreCase(oldName) ? newName : ss.getSubjectName();
            tracker.addLoadedSession(new StudySession(subjectName, ss.getTopicName(), ss.getMinutes(), ss.getDate()));
        }
    }

    private static void logStudySession() {
        Subject s = pickSubject();
        if (s == null) {
            return;
        }
        Topic t = pickTopic(s);
        if (t == null) {
            return;
        }
        int minutes = InputHelper.readInt(input, "Minutes studied: ");
        try {
            tracker.trackStudySession(s, t, minutes);
            System.out.println("Study session recorded.");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void recordQuiz() {
        Subject s = pickSubject();
        if (s == null) {
            return;
        }
        Topic t = pickTopic(s);
        if (t == null) {
            return;
        }
        int score = InputHelper.readInt(input, "Quiz score (0-100): ");
        int confidence = InputHelper.readInt(input, "Confidence level (1 = not confident, 5 = very confident): ");
        try {
            tracker.recordQuizScore(t, score, confidence);
            System.out.println("Quiz score and confidence level saved.");
        } catch (InvalidScoreException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void flashcardMenu() {
        Subject s = pickSubject();
        if (s == null) {
            return;
        }
        Topic t = pickTopic(s);
        if (t == null) {
            return;
        }
        boolean back = false;
        while (!back) {
            System.out.println(divider);
            System.out.println(" FLASHCARDS: " + t.getTopicName() + " (" + t.getFlashcardCount() + " cards)");
            System.out.println(divider);
            System.out.println("[1] -  Add a flashcard");
            System.out.println("[2] -  View all flashcards");
            System.out.println("[3] -  Review flashcards (practice)");
            System.out.println("[4] -  Delete a flashcard");
            System.out.println("[5] -  Back");
            int choice = InputHelper.readIntInRange(input, "Select an option (1-5): ", 1, 5);

            switch (choice) {
                case 1 -> addFlashcard(t);
                case 2 -> viewFlashcards(t);
                case 3 -> reviewFlashcards(t);
                case 4 -> deleteFlashcard(t);
                default -> back = true;
            }
        }
    }

    private static void addFlashcard(Topic topic) {
        String question = InputHelper.readText(input, "Question: ");
        String answer = InputHelper.readText(input, "Answer: ");
        try {
            topic.addFlashcard(new Flashcard(question, answer));
            System.out.println("Flashcard added.");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewFlashcards(Topic topic) {
        ArrayList<Flashcard> cards = topic.getFlashcards();
        if (cards.isEmpty()) {
            System.out.println("No flashcards yet. Choose [1] to add one.");
            return;
        }
        for (int i = 0; i < cards.size(); i++) {
            System.out.println("[" + (i + 1) + "] Q: " + cards.get(i).getQuestion());
            System.out.println("    A: " + cards.get(i).getAnswer());
        }
    }

    private static void deleteFlashcard(Topic topic) {
        if (topic.getFlashcardCount() == 0) {
            System.out.println("No flashcards to delete.");
            return;
        }
        viewFlashcards(topic);
        int choice = InputHelper.readIntInRange(input, "Number of the flashcard to delete (0 to cancel): ", 0, topic.getFlashcardCount());
        if (choice == 0) {
            return;
        }
        try {
            topic.removeFlashcard(choice - 1);
            System.out.println("Flashcard deleted.");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void reviewFlashcards(Topic topic) {
        ArrayList<Flashcard> deck = topic.getFlashcards();
        if (deck.isEmpty()) {
            System.out.println("No flashcards yet. Choose [1] to add one.");
            return;
        }
        int round = 1;
        while (!deck.isEmpty()) {
            System.out.println("--- Round " + round + " (" + deck.size() + " cards) ---");
            ArrayList<Flashcard> missed = new ArrayList<>();
            for (int i = 0; i < deck.size(); i++) {
                Flashcard card = deck.get(i);
                System.out.println("Card " + (i + 1) + " of " + deck.size());
                System.out.println("Q: " + card.getQuestion());
                InputHelper.readOptionalText(input, "Press Enter to show the answer...");
                System.out.println("A: " + card.getAnswer());
                if (!InputHelper.readYesNo(input, "Did you get it right? (y/n): ")) {
                    missed.add(card);
                }
            }
            System.out.println("Round result: " + (deck.size() - missed.size()) + " of " + deck.size() + " correct.");
            if (missed.isEmpty()) {
                System.out.println("Great job! You got every card right.");
                break;
            }
            if (!InputHelper.readYesNo(input, "Review the " + missed.size() + " missed card(s) again? (y/n): ")) {
                break;
            }
            deck = missed;
            round++;
        }
    }

    private static void practiceMenu() {
        Subject s = pickSubject();
        if (s == null) {
            return;
        }
        Topic t = pickTopic(s);
        if (t == null) {
            return;
        }
        boolean back = false;
        while (!back) {
            System.out.println(divider);
            System.out.println(" PRACTICE EXERCISES: " + t.getTopicName() + " (" + t.getProblemCount() + " problems)");
            System.out.println(divider);
            System.out.println("[1] -  Add a practice problem");
            System.out.println("[2] -  View all practice problems");
            System.out.println("[3] -  Start practice");
            System.out.println("[4] -  Delete a practice problem");
            System.out.println("[5] -  Back");
            int choice = InputHelper.readIntInRange(input, "Select an option (1-5): ", 1, 5);

            switch (choice) {
                case 1 -> addProblem(t);
                case 2 -> viewProblems(t);
                case 3 -> startPractice(t);
                case 4 -> deleteProblem(t);
                default -> back = true;
            }
        }
    }

    private static void addProblem(Topic topic) {
        String question = InputHelper.readText(input, "Problem: ");
        String answer = InputHelper.readText(input, "Correct answer: ");
        int level = InputHelper.readInt(input, "Level (1 = easy, 2 = medium, 3 = hard): ");
        try {
            topic.addProblem(new PracticeProblem(question, answer, level));
            System.out.println("Practice problem added.");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewProblems(Topic topic) {
        ArrayList<PracticeProblem> problems = topic.getProblems();
        if (problems.isEmpty()) {
            System.out.println("No practice problems yet. Choose [1] to add one.");
            return;
        }
        for (int i = 0; i < problems.size(); i++) {
            System.out.println("[" + (i + 1) + "] (" + problems.get(i).getLevelText() + ") " + problems.get(i).getQuestion());
            System.out.println("    Answer: " + problems.get(i).getAnswer());
        }
    }

    private static void deleteProblem(Topic topic) {
        if (topic.getProblemCount() == 0) {
            System.out.println("No practice problems to delete.");
            return;
        }
        viewProblems(topic);
        int choice = InputHelper.readIntInRange(input, "Number of the problem to delete (0 to cancel): ", 0, topic.getProblemCount());
        if (choice == 0) {
            return;
        }
        try {
            topic.removeProblem(choice - 1);
            System.out.println("Practice problem deleted.");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void startPractice(Topic topic) {
        ArrayList<PracticeProblem> problems = topic.getProblems();
        if (problems.isEmpty()) {
            System.out.println("No practice problems yet. Choose [1] to add one.");
            return;
        }
        int correct = 0;
        int number = 0;
        for (int level = 1; level <= 3; level++) {
            for (PracticeProblem p : problems) {
                if (p.getLevel() == level) {
                    number++;
                    System.out.println("Problem " + number + " of " + problems.size() + " (" + p.getLevelText() + ")");
                    System.out.println("Q: " + p.getQuestion());
                    String answer = InputHelper.readOptionalText(input, "Your answer: ");
                    if (p.isCorrect(answer)) {
                        System.out.println("Correct!");
                        correct++;
                    } else {
                        System.out.println("Not quite. The correct answer is: " + p.getAnswer());
                    }
                }
            }
        }
        int percent = correct * 100 / problems.size();
        System.out.println("Result: " + correct + " of " + problems.size() + " correct (" + percent + "%)");
        if (InputHelper.readYesNo(input, "Save " + percent + "% as a quiz score for this topic? (y/n): ")) {
            int confidence = InputHelper.readInt(input, "Confidence level (1 = not confident, 5 = very confident): ");
            try {
                tracker.recordQuizScore(topic, percent, confidence);
                System.out.println("Quiz score and confidence level saved.");
            } catch (InvalidScoreException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void spacedRepetitionMenu() {
        boolean back = false;
        while (!back) {
            System.out.println(divider);
            System.out.println(" SPACED REPETITION");
            System.out.println(divider);
            System.out.println("[1] -  Review a topic today");
            System.out.println("[2] -  View review schedule");
            System.out.println("[3] -  Back");
            int choice = InputHelper.readIntInRange(input, "Select an option (1-3): ", 1, 3);

            switch (choice) {
                case 1 -> reviewTopicToday();
                case 2 -> viewReviewSchedule();
                default -> back = true;
            }
        }
    }

    private static void reviewTopicToday() {
        Subject s = pickSubject();
        if (s == null) {
            return;
        }
        Topic t = pickTopic(s);
        if (t == null) {
            return;
        }
        System.out.println("Test yourself on \"" + t.getTopicName() + "\" without looking at your notes.");
        if (t.getFlashcardCount() > 0 && InputHelper.readYesNo(input, "Use your flashcards for this review? (y/n): ")) {
            reviewFlashcards(t);
        }
        boolean remembered = InputHelper.readYesNo(input, "Did you remember most of it? (y/n): ");
        t.markReviewed(remembered);
        if (remembered) {
            System.out.println("Nice! " + t.getReviewStatus() + ".");
        } else {
            System.out.println("That's okay, it will come back to you. " + t.getReviewStatus() + ".");
        }
    }

    private static void viewReviewSchedule() {
        boolean anyTopic = false;
        for (Subject s : subjects) {
            for (int i = 0; i < s.getTopicCount(); i++) {
                Topic t = s.getTopic(i);
                System.out.printf("%-18s (%s): %s%n", t.getTopicName(), s.getSubjectName(), t.getReviewStatus());
                anyTopic = true;
            }
        }
        if (!anyTopic) {
            System.out.println("No topics yet. Add a subject with topics first.");
        }
    }

    private static Subject pickSubject() {
        if (subjects.isEmpty()) {
            System.out.println("No subjects yet. Choose option 2 in the main menu to add one.");
            return null;
        }
        for (int i = 0; i < subjects.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + subjects.get(i).getSubjectName());
        }
        System.out.println("[0] Cancel");
        int choice = InputHelper.readIntInRange(input, "Choose a subject: ", 0, subjects.size());
        return choice == 0 ? null : subjects.get(choice - 1);
    }

    private static Topic pickTopic(Subject subject) {
        if (subject.getTopicCount() == 0) {
            System.out.println("This subject has no topics yet.");
            return null;
        }
        for (int i = 0; i < subject.getTopicCount(); i++) {
            System.out.println("[" + (i + 1) + "] " + subject.getTopic(i).getTopicName());
        }
        System.out.println("[0] Cancel");
        int choice = InputHelper.readIntInRange(input, "Choose a topic: ", 0, subject.getTopicCount());
        return choice == 0 ? null : subject.getTopic(choice - 1);
    }

    private static Subject findSubject(String name) {
        for (Subject s : subjects) {
            if (s.getSubjectName().equalsIgnoreCase(name.trim())) {
                return s;
            }
        }
        return null;
    }

    private static int nextSubjectId() {
        int max = 0;
        for (Subject s : subjects) {
            if (s.getId() > max) {
                max = s.getId();
            }
        }
        return max + 1;
    }

    private static boolean askBackToMainMenu() {
        System.out.println(divider);
        if (InputHelper.readYesNo(input, "Go back to the main menu? (y/n): ")) {
            return true;
        }
        if (InputHelper.readYesNo(input, "Are you sure you want to exit the program? (y/n): ")) {
            SaveData();
            return false;
        }
        return true;
    }

    public static void StartUp () {
        try {
            student = DataManager.load(subjects, tracker);
        } catch (IOException e) {
            System.out.println("Warning: could not read the saved data. Starting fresh.");
        }
        if (student == null) {
            System.out.println(divider);
            System.out.println(" Welcome to Studify! Let's set up your profile first.");
            System.out.println(divider);
            String name = InputHelper.readText(input, "Your name: ");
            int id = InputHelper.readInt(input, "Student ID (numbers only): ");
            String email = InputHelper.readOptionalText(input, "Email (press Enter to skip): ");
            if (email.isEmpty()) {
                student = new Student(id, name);
            } else {
                student = new Student(id, name, email);
            }
        }
        System.out.println();
        student.viewProfile();
        System.out.println();
    }

    public static void SaveData () {
        try {
            DataManager.save(student, subjects, tracker);
            System.out.println("Your data was saved to studify_data.txt. Goodbye, " + student.getName() + "!");
        } catch (IOException e) {
            System.out.println("Error: could not save your data (" + e.getMessage() + ").");
        }
    }

    public static void main(String[] args){
        StartUp();
        boolean running = true;

        while(running) {
            running = MainMenu();
        }


    }
}
