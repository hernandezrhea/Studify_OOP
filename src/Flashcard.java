class Flashcard {
    private String question;
    private String answer;

    public Flashcard (String question, String answer) throws Main.InvalidInputException {
        if (question == null || question.trim().isEmpty() || answer == null || answer.trim().isEmpty()) {
            throw new Main.InvalidInputException("A flashcard needs both a question and an answer.");
        }
        this.question = question.trim();
        this.answer = answer.trim();
    }

    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
}
