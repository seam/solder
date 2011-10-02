package org.jboss.seam.config.examples.quiz;

import java.io.Serializable;

import javax.enterprise.inject.Instance;
import org.jboss.seam.international.status.Messages;

/**
 * Representation of historic quiz configured by XML
 * 
 * @author <a href="mailto:tremes@redhat.com"> Tomas Remes </a>
 * 
 */
public class HistoricQuiz implements Quiz, Serializable {

    private int score = 0;
    private Instance<Question> questions;
    private int questionNumber = 1;
    private int selectedAnswer;
    private Messages messages;

    public HistoricQuiz() {

    }

    public HistoricQuiz(Instance<Question> questions) {
        this.questions = questions;
    }

    public boolean isNextQuestionAvailable() {
        return getActualQuestion() != null;
    }

    public int getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(int selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public Question getActualQuestion() {
        Question actualQuestion = null;
        for (Question q : questions) {
            if (q.getId() == questionNumber) {
                actualQuestion = q;

            }
        }

        return actualQuestion;
    }

    public int getScore() {
        return score;
    }

    public Instance<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Instance<Question> questions) {
        this.questions = questions;
    }

    public void saveQuestion() {
        Answer answer = null;

        for (Answer a : getActualQuestion().getAnswers()) {
            if (a.getId() == selectedAnswer) {
                answer = a;
            }
        }
        if (answer != null && answer.isCorrect()) {

            messages.info("Excellent! You're right!");
            score = score + 10;
        } else {
            messages.info("Wrong answer!");
        }
       
        getActualQuestion().setSaved(true);
        setSelectedAnswer(0);
        
    }

    public void nextQuestion() {
        questionNumber++;
        setSelectedAnswer(0);

    }

    public void resetQuiz() {
        questionNumber = 1;
        score = 0;
        selectedAnswer = 0;
        for(Question q : questions){
            q.setSaved(false);
        }
    }

}
