package org.examemulator.domain.exam;

import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.util.domain.GenericRepository;

import jakarta.data.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;

@Repository
@ApplicationScoped
public class ExamRepository extends GenericRepository<Exam, Long> {

    @SuppressWarnings("unchecked")
    public Stream<Exam> findByCertification(final Certification certification) {
	
	final var qlString = """
			SELECT DISTINCT ex.*
			  FROM EXAM ex
		         INNER JOIN EXAM_QUESTION exq on exq.EXAM_QUESTION_ID = ex.ID
		         INNER JOIN QUESTION q on q.id = exq.QUESTION_ID
		         INNER JOIN QUESTIONNAIRE qre on qre.id = q.QUESTIONNAIRE_ID
		         WHERE qre.CERTIFICATION_ID = :certification
				""";

	final var query = entityManager.createNativeQuery(qlString, Exam.class);
	query.setParameter("certification", certification.getId());

	return query.getResultStream();
    }
}
