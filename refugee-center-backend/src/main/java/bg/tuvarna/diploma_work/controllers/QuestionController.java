package bg.tuvarna.diploma_work.controllers;

import bg.tuvarna.diploma_work.enumerables.QuestionState;
import bg.tuvarna.diploma_work.exceptions.InternalErrorResponseStatusException;
import bg.tuvarna.diploma_work.models.Question;
import bg.tuvarna.diploma_work.services.LogService;
import bg.tuvarna.diploma_work.services.MailService;
import bg.tuvarna.diploma_work.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
public class QuestionController {

    @Autowired
    private LogService logService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private MailService mailService;

    @PostMapping("/send-question")
    public ResponseEntity<Void> sendQuestion(@RequestBody Question question) {

        question.setId(0L);
        question.setDateReceived(LocalDate.now());
        question.setQuestionState(QuestionState.Pending);


        if( questionService.save( question ) == null ){
            logService.logErrorMessage("QuestionService::save", question.getId() );
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-questions")
    public List<Question> getQuestions(){

        return questionService.getQuestions();
    }

    @PutMapping("/set-as-reserved/{id}")
    @Transactional
    public void setAsReserved( @PathVariable long id){

        questionService.setAsReserved(id);
    }

    @PutMapping("/free-question/{id}")
    @Transactional
    public void freeQuestion( @PathVariable long id){

        questionService.freeQuestion(id);
    }

    @PostMapping("/delete-selected-questions")
    @Transactional
    public ResponseEntity<Void> deleteSelectedQuestions(@RequestBody List<Long> questions){

        questionService.deleteQuestions(questions);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/answer-question")
    @Transactional
    public ResponseEntity<Void> answerQuestion(@RequestBody Question question){

        question.setQuestionState(QuestionState.Answered);
        question = questionService.save(question);
        if( question == null ){
            logService.logErrorMessage("QuestionService::save", question.getId() );
            throw new InternalErrorResponseStatusException();
        }

        if( !mailService.sendQuestionAnswer(question) ){
            logService.logErrorMessage("MailService::sendQuestionAnswer", question.getId() );
            throw new InternalErrorResponseStatusException();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
