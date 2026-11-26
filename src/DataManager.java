import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;

class DataManager {
    private static final String FILE_NAME = "studify_data.txt";

    public static void save(Student student, ArrayList<Subject> subjects, StudyTracker tracker) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            out.println("STUDENT|" + student.getId() + "|" + student.getName() + "|" + student.getEmail());
            for (Subject s : subjects) {
                out.println("SUBJECT|" + s.getId() + "|" + s.getSubjectName() + "|" + dateToText(s.getAssessmentDate()));
                for (int i = 0; i < s.getTopicCount(); i++) {
                    Topic t = s.getTopic(i);
                    out.println("TOPIC|" + t.getTopicName() + "|" + t.getDifficulty() + "|" + t.getConfidence()
                            + "|" + dateToText(t.getAssessmentDate()) + "|" + scoresToText(t));
                    for (Flashcard card : t.getFlashcards()) {
                        out.println("CARD|" + card.getQuestion() + "|" + card.getAnswer());
                    }
                    for (PracticeProblem pp : t.getProblems()) {
                        out.println("PROBLEM|" + pp.getQuestion() + "|" + pp.getAnswer() + "|" + pp.getLevel());
                    }
                    if (t.getReviewCount() > 0) {
                        out.println("REVIEW|" + t.getReviewStage() + "|" + dateToText(t.getNextReviewDate()) + "|" + t.getReviewCount());
                    }
                }
            }
            for (StudySession ss : tracker.getSessions()) {
                out.println("SESSION|" + ss.getSubjectName() + "|" + ss.getTopicName() + "|" + ss.getMinutes() + "|" + ss.getDate());
            }
            if (out.checkError()) {
                throw new IOException("the file could not be written");
            }
        }
    }

    public static Student load(ArrayList<Subject> subjects, StudyTracker tracker) throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return null;
        }
        Student student = null;
        Subject current = null;
        Topic lastTopic = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] p = line.split("\\|", -1);
                try {
                    switch (p[0]) {
                        case "STUDENT" -> student = new Student(Integer.parseInt(p[1]), p[2], p[3]);
                        case "SUBJECT" -> {
                            current = new Subject(Integer.parseInt(p[1]), p[2], textToDate(p[3]));
                            subjects.add(current);
                        }
                        case "TOPIC" -> {
                            lastTopic = null;
                            Topic t = new Topic(p[1], Integer.parseInt(p[2]), textToDate(p[4]));
                            if (!p[5].equals("-")) {
                                for (String score : p[5].split(",")) {
                                    t.addQuizScore(Integer.parseInt(score));
                                }
                            }
                            int confidence = Integer.parseInt(p[3]);
                            if (confidence > 0) {
                                t.setConfidence(confidence);
                            }
                            if (current != null) {
                                current.addTopic(t);
                                lastTopic = t;
                            }
                        }
                        case "CARD" -> {
                            if (lastTopic != null) {
                                lastTopic.addFlashcard(new Flashcard(p[1], p[2]));
                            }
                        }
                        case "PROBLEM" -> {
                            if (lastTopic != null) {
                                lastTopic.addProblem(new PracticeProblem(p[1], p[2], Integer.parseInt(p[3])));
                            }
                        }
                        case "REVIEW" -> {
                            if (lastTopic != null) {
                                lastTopic.setReviewData(Integer.parseInt(p[1]), textToDate(p[2]), Integer.parseInt(p[3]));
                            }
                        }
                        case "SESSION" -> tracker.addLoadedSession(
                                new StudySession(p[1], p[2], Integer.parseInt(p[3]), LocalDate.parse(p[4])));
                        default -> { }
                    }
                } catch (Exception e) {
                }
            }
        }
        return student;
    }

    private static String dateToText(LocalDate date) {
        return date == null ? "-" : date.toString();
    }

    private static LocalDate textToDate(String text) {
        return text.equals("-") ? null : LocalDate.parse(text);
    }

    private static String scoresToText(Topic t) {
        if (!t.hasQuizScore()) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (int score : t.getQuizScores()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(score);
        }
        return sb.toString();
    }
}
