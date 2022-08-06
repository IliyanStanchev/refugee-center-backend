package bg.tuvarna.diploma_work.models;

import bg.tuvarna.diploma_work.enumerables.QuestionState;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "QUESTIONS")
public class Question implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @Column(nullable = false, length = 64)
    private String email;


    @Column(nullable = false, length = 64)
    private String name;


    @Column(nullable = false, length = 256)
    private String message;


    @Column(nullable = false)
    private LocalDate dateReceived;


    @Column(nullable = false)
    private QuestionState questionState;


    @Column(nullable = false, length = 256)
    private String answer;

    public Question() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public QuestionState getQuestionState() {
        return questionState;
    }


    public void setQuestionState(QuestionState questionState) {
        this.questionState = questionState;
    }

    public LocalDate getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(LocalDate dateReceived) {
        this.dateReceived = dateReceived;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}