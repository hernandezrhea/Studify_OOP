class PracticeExerciseStrategy extends StudyStrategy {

    public PracticeExerciseStrategy () {
        super("Practice Exercises");
    }

    @Override
    public boolean isSuitable(Topic topic) {
        if (!topic.hasQuizScore()) {
            return topic.getDifficulty() >= 3;
        }
        if (isWeak(topic)) {
            return topic.getDifficulty() >= 4;
        }
        if (isStrong(topic)) {
            return false;
        }
        int days = topic.getDaysUntilAssessment();
        return days >= 0 && days <= 14;
    }

    @Override
    public String recommend(Topic topic) {
        String tip = "Solve practice problems on " + topic.getTopicName()
                + ". Start with easy ones, then move to harder ones, and redo every item you got wrong.";
        if (topic.getDifficulty() >= 4) {
            tip += " This topic is hard, so work through at least 10 problems per session.";
        }
        if (topic.getProblemCount() == 0) {
            tip += " You have no practice problems for this topic yet. Add some in option 9, then Practice Exercises.";
        } else {
            tip += " You have " + topic.getProblemCount() + " practice problem(s) saved. Try them in option 9, then Practice Exercises.";
        }
        return tip;
    }
}
