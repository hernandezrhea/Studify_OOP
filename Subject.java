import java.time.LocalDate;

class Subject {
    private int Id;
    private String SubjectName;
    private Topic[] Topics = new Topic[20];
    private int topicCount = 0;
    private LocalDate assessmentDate;

    public Subject (){ // Default
        this.Id = 0;
        this.SubjectName = "Unknown";
    }

    public Subject (int Id, String SubjectName){ // ID and name only
        this.Id = Id;
        this.SubjectName = SubjectName;
    }

    public Subject (int Id, String SubjectName, LocalDate assessmentDate){
        this.Id = Id;
        this.SubjectName = SubjectName;
        this.assessmentDate = assessmentDate;
    }

    public int getId() { return Id; }

    public String getSubjectName() { return SubjectName; }
    public void setSubjectName(String SubjectName) throws Main.InvalidInputException {
        if (SubjectName == null || SubjectName.trim().isEmpty()) {
            throw new Main.InvalidInputException("Subject name cannot be empty.");
        }
        this.SubjectName = SubjectName.trim();
        for (int i = 0; i < topicCount; i++) {
            Topics[i].setSubjectName(this.SubjectName);
        }
    }

    public LocalDate getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(LocalDate assessmentDate) { this.assessmentDate = assessmentDate; }

    public int getTopicCount() { return topicCount; }
    public int getMaxTopics() { return Topics.length; }
    public boolean isFull() { return topicCount >= Topics.length; }
    public Topic getTopic(int index) { return Topics[index]; }

    public void addTopic(Topic topic) throws Main.InvalidInputException {
        if (isFull()) {
            throw new Main.InvalidInputException("This subject already has the maximum of " + Topics.length + " topics.");
        }
        if (findTopic(topic.getTopicName()) != null) {
            throw new Main.InvalidInputException("That topic already exists in this subject.");
        }
        topic.setSubjectName(SubjectName);
        Topics[topicCount] = topic;
        topicCount++;
    }

    public Topic findTopic(String topicName) {
        for (int i = 0; i < topicCount; i++) {
            if (Topics[i].getTopicName().equalsIgnoreCase(topicName.trim())) {
                return Topics[i];
            }
        }
        return null;
    }

    public void removeTopic(String topicName) throws Main.InvalidInputException {
        for (int i = 0; i < topicCount; i++) {
            if (Topics[i].getTopicName().equalsIgnoreCase(topicName.trim())) {
                for (int j = i; j < topicCount - 1; j++) {
                    Topics[j] = Topics[j + 1];
                }
                Topics[topicCount - 1] = null;
                topicCount--;
                return;
            }
        }
        throw new Main.InvalidInputException("Topic \"" + topicName + "\" does not exist.");
    }

    public void updateSubject(String newName, LocalDate newDate) throws Main.InvalidInputException {
        if (newName != null && !newName.trim().isEmpty()) {
            setSubjectName(newName);
        }
        if (newDate != null) {
            this.assessmentDate = newDate;
        }
    }
}
