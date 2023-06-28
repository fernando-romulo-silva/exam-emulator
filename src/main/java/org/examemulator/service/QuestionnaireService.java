package org.examemulator.service;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

import java.util.List;
import java.util.stream.Stream;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.QuestionnaireRepository;
import org.examemulator.domain.questionnaire.question.Question;
import org.examemulator.domain.questionnaire.question.QuestionConcept;
import org.examemulator.domain.questionnaire.question.QuestionConceptRepository;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional(SUPPORTS)
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;

    private final QuestionConceptRepository questionConceptRepository;

    @Inject
    QuestionnaireService( //
		    final QuestionnaireRepository questionnaireRepository, //
		    final QuestionConceptRepository questionConceptRepository) {
	super();
	this.questionnaireRepository = questionnaireRepository;
	this.questionConceptRepository = questionConceptRepository;
    }

    public record QuestionnaireDTO(Integer order, String name, String description) {}
    
    @Transactional(REQUIRED)
    public Questionnaire saveOrUpdateQuestionnaire(final QuestionnaireDTO data, final List<Question> questionsFromFile, final QuestionnaireSet questionnaireSet) {

	final var optionalQuestionnaire = questionnaireRepository.findByOrderAndQuestionnaireSet(data.order(), questionnaireSet);

	if (optionalQuestionnaire.isEmpty()) {

	    final var questionnaireTemp = new Questionnaire( //
			    data.name(), //
			    data.description(), //
			    data.order(), //
			    questionnaireSet, //
			    questionsFromFile //
	    ); //

	    return questionnaireRepository.save(questionnaireTemp);
	}

	final var questionnaire = optionalQuestionnaire.get();
	questionnaire.update(data.order(), data.name(), questionnaireSet, questionsFromFile);
	return questionnaireRepository.update(questionnaire);
    }

    @Transactional(REQUIRED)
    public QuestionConcept readOrSaveQuestionConcept(final String questionConceptName, final Certification certification) {

	final var optionalQuestionConcept = questionConceptRepository.findByNameAndCertification(questionConceptName, certification);

	if (optionalQuestionConcept.isEmpty()) {
	    final var questionnaireSetTemp = new QuestionConcept(questionConceptName, certification);
	    return questionConceptRepository.save(questionnaireSetTemp);
	}

	return optionalQuestionConcept.get();
    }

    public Stream<Questionnaire> findByCertificationAndQuestionnaireSet(final Certification certification, final QuestionnaireSet questionnaireSet) {
	return questionnaireRepository.findByCertificationAndQuestionnaireSet(certification, questionnaireSet);
    }

    public Stream<Question> findByCertification(Certification selectedCertification) {
	return questionnaireRepository.findByCertification(selectedCertification);
    }
}
