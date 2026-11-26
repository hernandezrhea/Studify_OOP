import java.time.LocalDate;

class StudySession {
    private String subjectName;
    private String topicName;
    private int minutes;
    private LocalDate date;

    public StudySession (String subjectName, String topicName, int minutes, LocalDate date) {
        this.subjectName = subjectName;
        this.topicName = topicName;
        this.minutes = minutes;
        this.date = date;
    }

    public String getSubjectName() { return subjectName; }
    public String getTopicName() { return topicName; }
    public int getMinutes() { return minutes; }
    public LocalDate getDate() { return date; }
}
