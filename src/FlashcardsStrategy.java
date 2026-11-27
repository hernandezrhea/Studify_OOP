class FlashcardsStrategy extends StudyStrategy {

    public FlashcardsStrategy () {
        super("Flashcards");
    }

    @Override
    public boolean isSuitable(Topic topic) {
        if (!topic.hasQuizScore()) {
            return topic.getDifficulty() <= 2;
        }
        return isWeak(topic) && topic.getDifficulty() <= 3;
    }

    @Override
    public String recommend(Topic topic) {
        String tip = "Make flashcards for the key terms, formulas, and ideas in " + topic.getTopicName()
                + ". Go through them for 15 to 20 minutes a day and put aside the cards you miss, then review those again.";
        int days = topic.getDaysUntilAssessment();
        if (days >= 0 && days <= 7) {
            tip += " Your assessment is close, so do a short round every day until then.";
        }
        if (topic.getFlashcardCount() == 0) {
            tip += " You have no flashcards for this topic yet. Add some in option 9, then Flashcards.";
        } else {
            tip += " You already have " + topic.getFlashcardCount() + " flashcard(s) saved. Practice them in option 9, then Flashcards.";
        }
        return tip;
    }
}
