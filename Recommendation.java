import java.util.ArrayList;

class Recommendation {
    private int topicDifficulty;
    private int priorityLevel;
    private String recommendedMethod;

    private StudyStrategy[] strategies = {
        new FlashcardsStrategy(),
        new PracticeExerciseStrategy(),
        new SpacedRepetitionStrategy()
    };

    public int getPriorityScore(Topic topic) {
        int score = topic.hasQuizScore() ? topic.getLatestScore() : 50;
        int confidence = topic.getConfidence() > 0 ? topic.getConfidence() : 3;

        double scorePart = (100 - score) * 0.30;
        double confidencePart = (5 - confidence) / 4.0 * 25;
        double difficultyPart = (topic.getDifficulty() - 1) / 4.0 * 20;

        int days = topic.getDaysUntilAssessment();
        double urgencyPart;
        if (days < 0) {
            urgencyPart = 0;
        } else if (days <= 3) {
            urgencyPart = 25;
        } else if (days <= 7) {
            urgencyPart = 20;
        } else if (days <= 14) {
            urgencyPart = 12;
        } else if (days <= 30) {
            urgencyPart = 6;
        } else {
            urgencyPart = 0;
        }

        return (int) Math.round(scorePart + confidencePart + difficultyPart + urgencyPart);
    }

    public String getPriorityLabel(int priorityScore) {
        if (priorityScore >= 60) {
            return "HIGH";
        } else if (priorityScore >= 35) {
            return "MEDIUM";
        }
        return "LOW";
    }

    public String analyzePerformance(Topic topic) {
        topicDifficulty = topic.getDifficulty();
        priorityLevel = getPriorityScore(topic);
        recommendedMethod = suggestStudyMethod(topic).getStrategyName();
        return getPriorityLabel(priorityLevel);
    }

    public ArrayList<Topic> prioritizeTopics(ArrayList<Subject> subjects) {
        ArrayList<Topic> all = new ArrayList<>();
        for (Subject s : subjects) {
            for (int i = 0; i < s.getTopicCount(); i++) {
                all.add(s.getTopic(i));
            }
        }
        all.sort((a, b) -> getPriorityScore(b) - getPriorityScore(a));
        return all;
    }

    public StudyStrategy suggestStudyMethod(Topic topic) {
        for (StudyStrategy strategy : strategies) {
            if (strategy.isSuitable(topic)) {
                return strategy;
            }
        }
        return strategies[strategies.length - 1];
    }

    public void generateReport(ArrayList<Subject> subjects, StudyTracker tracker) {
        String line = "-------------------------------------------------------------------";
        System.out.println(line);
        System.out.println(" PROGRESS REPORT");
        System.out.println(line);

        int totalMinutes = tracker.getStudyDuration();
        int totalQuizzes = 0;
        for (Subject s : subjects) {
            for (int i = 0; i < s.getTopicCount(); i++) {
                totalQuizzes += s.getTopic(i).getQuizCount();
            }
        }
        System.out.println("Total study time     : " + totalMinutes + " minutes (" + (totalMinutes / 60) + "h " + (totalMinutes % 60) + "m)");
        System.out.println("Completed sessions   : " + tracker.getCompletedSessions());
        System.out.println("Quiz scores recorded : " + totalQuizzes);

        if (subjects.isEmpty()) {
            System.out.println();
            System.out.println("No subjects yet. Add a subject first.");
            return;
        }

        ArrayList<String> needsWork = new ArrayList<>();
        for (Subject s : subjects) {
            System.out.println();
            System.out.println("Subject: " + s.getSubjectName() + "  (Assessment: " + InputHelper.formatDate(s.getAssessmentDate()) + ")");
            if (s.getTopicCount() == 0) {
                System.out.println("  No topics yet.");
                continue;
            }
            System.out.printf("  %-18s %-8s %-7s %-6s %-10s %s%n", "Topic", "Score", "Change", "Conf", "Time(min)", "Priority");
            for (int i = 0; i < s.getTopicCount(); i++) {
                Topic t = s.getTopic(i);
                String change = "-";
                if (t.getQuizCount() >= 2) {
                    int diff = t.getLatestScore() - t.getFirstScore();
                    change = (diff > 0 ? "+" : "") + diff;
                }
                int minutes = tracker.getMinutesForTopic(s.getSubjectName(), t.getTopicName());
                String label = analyzePerformance(t);
                System.out.printf("  %-18s %-8s %-7s %-6s %-10d %s%n",
                        t.getTopicName(), t.getScoreText(), change, t.getConfidenceText(), minutes, label);
                if (label.equals("HIGH")) {
                    needsWork.add(t.getTopicName() + " (" + s.getSubjectName() + ")");
                }
            }
        }

        System.out.println();
        System.out.println("Areas that still need work:");
        if (needsWork.isEmpty()) {
            System.out.println("  None right now. Keep it up!");
        } else {
            for (String item : needsWork) {
                System.out.println("  - " + item);
            }
        }
    }

    public int getTopicDifficulty() { return topicDifficulty; }
    public int getPriorityLevel() { return priorityLevel; }
    public String getRecommendedMethod() { return recommendedMethod; }
}
