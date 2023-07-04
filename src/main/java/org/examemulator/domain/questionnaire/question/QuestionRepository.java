package org.examemulator.domain.questionnaire.question;

import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.util.domain.GenericRepository;

import jakarta.data.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;

@Repository
@ApplicationScoped
public class QuestionRepository extends GenericRepository<Question, Long> {

    public Stream<Question> findByCertificationAndQuestionnaireSet(final Certification certification) {

	final var qlString = """
			select q
			  from QUESTION q
			 where q.certification = :certification
			   and q.set = :set
				""";

	final var query = entityManager.createNativeQuery(qlString, Question.class);
	query.setParameter("certification", certification);

	return query.getResultStream();
    }
}
