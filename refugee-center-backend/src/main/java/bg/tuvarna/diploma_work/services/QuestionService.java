package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.Question;
import bg.tuvarna.diploma_work.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public Question save( Question question ) {

        return questionRepository.save( question );
    }
}
