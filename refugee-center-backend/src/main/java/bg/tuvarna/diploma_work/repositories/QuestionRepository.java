package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
