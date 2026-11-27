import java.time.LocalDate;
import java.util.ArrayList;

class StudyTracker {
    private int studyDuration = 0;
    private int completedSessions = 0;
    private ArrayList<StudySession> sessions = new ArrayList<>();

    public void recordQuizScore(Topic topic, int score, int confidence) throws Main.InvalidScoreException {
        if (score < 0 || score > 100) {
            throw new Main.InvalidScoreException("Quiz score must be from 0 to 100.");
        }
        if (confidence < 1 || confidence > 5) {
            throw new Main.InvalidScoreException("Confidence level must be from 1 to 5.");
        }
        topic.addQuizScore(score);
        topic.setConfidence(confidence);
    }

    public void trackStudySession(Subject subject, Topic topic, int minutes) throws Main.InvalidInputException {
        if (minutes < 1 || minutes > 600) {
            throw new Main.InvalidInputException("Study time must be from 1 to 600 minutes.");
        }
        sessions.add(new StudySession(subject.getSubjectName(), topic.getTopicName(), minutes, LocalDate.now()));
        updateProgress();
    }

    public void addLoadedSession(StudySession session) {
        sessions.add(session);
        updateProgress();
    }

    public void updateProgress() {
        studyDuration = 0;
        for (StudySession s : sessions) {
            studyDuration += s.getMinutes();
        }
        completedSessions = sessions.size();
    }

    public int getMinutesForTopic(String subjectName, String topicName) {
        int total = 0;
        for (StudySession s : sessions) {
            if (s.getSubjectName().equalsIgnoreCase(subjectName) && s.getTopicName().equalsIgnoreCase(topicName)) {
                total += s.getMinutes();
            }
        }
        return total;
    }

    public void removeSessions(String subjectName, String topicName) {
        sessions.removeIf(s -> s.getSubjectName().equalsIgnoreCase(subjectName)
                && (topicName == null || s.getTopicName().equalsIgnoreCase(topicName)));
        updateProgress();
    }

    public int getStudyDuration() { return studyDuration; }
    public int getCompletedSessions() { return completedSessions; }
    public ArrayList<StudySession> getSessions() { return sessions; }
}
