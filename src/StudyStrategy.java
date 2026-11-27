abstract class StudyStrategy {
    private String strategyName;

    public StudyStrategy (String strategyName) {
        this.strategyName = strategyName;
    }

    public String getStrategyName() { return strategyName; }

    protected boolean isWeak(Topic topic) {
        return topic.hasQuizScore() && (topic.getLatestScore() < 60 || topic.getConfidence() <= 2);
    }

    protected boolean isStrong(Topic topic) {
        return topic.hasQuizScore() && topic.getLatestScore() >= 75 && topic.getConfidence() >= 4;
    }

    public abstract boolean isSuitable(Topic topic);

    public abstract String recommend(Topic topic);
}
