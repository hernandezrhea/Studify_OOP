class PracticeProblem {
    private String question;
    private String answer;
    private int level;

    public PracticeProblem (String question, String answer, int level) throws Main.InvalidInputException {
        if (question == null || question.trim().isEmpty() || answer == null || answer.trim().isEmpty()) {
            throw new Main.InvalidInputException("A practice problem needs both a question and an answer.");
        }
        if (level < 1 || level > 3) {
            throw new Main.InvalidInputException("Level must be 1 (easy), 2 (medium), or 3 (hard).");
        }
        this.question = question.trim();
        this.answer = answer.trim();
        this.level = level;
    }

    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public int getLevel() { return level; }

    public String getLevelText() {
        if (level == 1) {
            return "Easy";
        } else if (level == 2) {
            return "Medium";
        }
        return "Hard";
    }

    public boolean isCorrect(String userAnswer) {
        return answer.equalsIgnoreCase(userAnswer.trim());
    }
}
