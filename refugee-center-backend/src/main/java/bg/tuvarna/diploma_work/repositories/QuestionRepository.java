package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.enumerables.QuestionState;
import bg.tuvarna.diploma_work.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.questionState = 0")
    List<Question> getQuestions();

    @Modifying
    @Query("UPDATE Question q SET q.questionState = ?2 WHERE q.id = ?1")
    void changeQuestionState(long id, QuestionState reserved);

    @Modifying
    @Query("DELETE FROM Question q WHERE q.id IN ( :questions )")
    void deleteQuestions(List<Long> questions);
}
