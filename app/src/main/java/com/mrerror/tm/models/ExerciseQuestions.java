package com.mrerror.tm.models;

/**
 * Created by kareem on 9/12/2017.
 */

public class ExerciseQuestions {

    int id ;
    String question;
    String answer;
    int type;

    public void setId(int id) {
        this.id = id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getType() {
        return type;
    }

    public String getAnswer() {
        return answer;
    }

}
