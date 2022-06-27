package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.enumerables.QuestionState;
import bg.tuvarna.diploma_work.models.Question;
import bg.tuvarna.diploma_work.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public Question save(Question question) {

        return questionRepository.save(question);
    }

    public List<Question> getQuestions() {

        return questionRepository.getQuestions();
    }

    public void setAsReserved(long id) {

        questionRepository.changeQuestionState(id, QuestionState.Reserved);
    }

    public void deleteQuestions(List<Long> questions) {

        questionRepository.deleteQuestions(questions);
    }

    public void freeQuestion(long id) {

        questionRepository.changeQuestionState(id, QuestionState.Pending);
    }
}
