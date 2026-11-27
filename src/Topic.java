import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

class Topic {
    private String topicName;
    private String subjectName = "";
    private int difficulty;
    private int confidence = 0;
    private ArrayList<Integer> quizScores = new ArrayList<>();
    private LocalDate assessmentDate;
    private ArrayList<Flashcard> flashcards = new ArrayList<>();
    private ArrayList<PracticeProblem> problems = new ArrayList<>();
    private static final int[] REVIEW_DAYS = {1, 3, 7, 14};
    private int reviewStage = 0;
    private int reviewCount = 0;
    private LocalDate nextReviewDate;

    public Topic (String topicName, int difficulty, LocalDate assessmentDate) throws Main.InvalidInputException {
        setTopicName(topicName);
        setDifficulty(difficulty);
        this.assessmentDate = assessmentDate;
    }

    public String getTopicName() { return topicName; }
    public void setTopicName(String topicName) throws Main.InvalidInputException {
        if (topicName == null || topicName.trim().isEmpty()) {
            throw new Main.InvalidInputException("Topic name cannot be empty.");
        }
        this.topicName = topicName.trim();
    }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) throws Main.InvalidInputException {
        if (difficulty < 1 || difficulty > 5) {
            throw new Main.InvalidInputException("Difficulty must be from 1 to 5.");
        }
        this.difficulty = difficulty;
    }

    public int getConfidence() { return confidence; }
    public void setConfidence(int confidence) throws Main.InvalidScoreException {
        if (confidence < 1 || confidence > 5) {
            throw new Main.InvalidScoreException("Confidence level must be from 1 to 5.");
        }
        this.confidence = confidence;
    }

    public void addQuizScore(int score) throws Main.InvalidScoreException {
        if (score < 0 || score > 100) {
            throw new Main.InvalidScoreException("Quiz score must be from 0 to 100.");
        }
        quizScores.add(score);
    }

    public boolean hasQuizScore() { return !quizScores.isEmpty(); }
    public int getQuizCount() { return quizScores.size(); }
    public int getLatestScore() { return quizScores.isEmpty() ? -1 : quizScores.get(quizScores.size() - 1); }
    public int getFirstScore() { return quizScores.isEmpty() ? -1 : quizScores.get(0); }
    public ArrayList<Integer> getQuizScores() { return new ArrayList<>(quizScores); }

    public LocalDate getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(LocalDate assessmentDate) { this.assessmentDate = assessmentDate; }

    public int getDaysUntilAssessment() {
        if (assessmentDate == null) {
            return Integer.MAX_VALUE;
        }
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), assessmentDate);
    }

    public void addFlashcard(Flashcard card) { flashcards.add(card); }

    public void removeFlashcard(int index) throws Main.InvalidInputException {
        if (index < 0 || index >= flashcards.size()) {
            throw new Main.InvalidInputException("That flashcard does not exist.");
        }
        flashcards.remove(index);
    }

    public ArrayList<Flashcard> getFlashcards() { return new ArrayList<>(flashcards); }
    public int getFlashcardCount() { return flashcards.size(); }

    public void addProblem(PracticeProblem problem) { problems.add(problem); }

    public void removeProblem(int index) throws Main.InvalidInputException {
        if (index < 0 || index >= problems.size()) {
            throw new Main.InvalidInputException("That practice problem does not exist.");
        }
        problems.remove(index);
    }

    public ArrayList<PracticeProblem> getProblems() { return new ArrayList<>(problems); }
    public int getProblemCount() { return problems.size(); }

    public void markReviewed(boolean remembered) {
        if (!remembered) {
            reviewStage = 0;
        }
        nextReviewDate = LocalDate.now().plusDays(REVIEW_DAYS[reviewStage]);
        if (reviewStage < REVIEW_DAYS.length - 1) {
            reviewStage++;
        }
        reviewCount++;
    }

    public void setReviewData(int reviewStage, LocalDate nextReviewDate, int reviewCount) {
        this.reviewStage = reviewStage;
        this.nextReviewDate = nextReviewDate;
        this.reviewCount = reviewCount;
    }

    public int getReviewStage() { return reviewStage; }
    public int getReviewCount() { return reviewCount; }
    public LocalDate getNextReviewDate() { return nextReviewDate; }

    public String getReviewStatus() {
        if (nextReviewDate == null) {
            return "Not started yet";
        }
        int days = (int) ChronoUnit.DAYS.between(LocalDate.now(), nextReviewDate);
        if (days <= 0) {
            return "DUE now";
        }
        return "Next review in " + days + " day(s) (" + nextReviewDate + ")";
    }

    public String getScoreText() { return hasQuizScore() ? getLatestScore() + "/100" : "N/A"; }
    public String getConfidenceText() { return confidence > 0 ? confidence + "/5" : "N/A"; }
}
