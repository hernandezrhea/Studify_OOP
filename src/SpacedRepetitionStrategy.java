class SpacedRepetitionStrategy extends StudyStrategy {

    public SpacedRepetitionStrategy () {
        super("Spaced Repetition");
    }

    @Override
    public boolean isSuitable(Topic topic) {
        return true;
    }

    @Override
    public String recommend(Topic topic) {
        String tip = "Review " + topic.getTopicName()
                + " again after 1 day, 3 days, 7 days, and 14 days. Test yourself each time instead of just rereading your notes.";
        int days = topic.getDaysUntilAssessment();
        if (days >= 0 && days <= 3) {
            tip += " Your assessment is very near, so do one quick review today and one the day before.";
        }
        tip += " Review status: " + topic.getReviewStatus() + ". Track your reviews in option 9, then Spaced Repetition.";
        return tip;
    }
}
