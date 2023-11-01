package org.examemulator.application;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.QuestionnaireRepository;
import org.examemulator.domain.questionnaire.question.Question;
import org.examemulator.domain.questionnaire.question.QuestionConcept;
import org.examemulator.domain.questionnaire.question.QuestionConceptRepository;
import org.examemulator.domain.questionnaire.question.QuestionRepository;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.infra.dto.QuestionDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional(SUPPORTS)
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;

    private final QuestionRepository questionRepository;

    private final QuestionConceptRepository questionConceptRepository;

    @Inject
    QuestionnaireService( //
		    final QuestionnaireRepository questionnaireRepository, //
		    final QuestionRepository questionRepository, //
		    final QuestionConceptRepository questionConceptRepository) {
	super();
	this.questionnaireRepository = questionnaireRepository;
	this.questionRepository = questionRepository;
	this.questionConceptRepository = questionConceptRepository;
    }

    public record QuestionnaireDTO(Integer order, String name, String description) {
    }

    @Transactional(REQUIRED)
    public Questionnaire saveOrUpdateQuestionnaire(final QuestionnaireDTO data, final List<Question> questionsFromFile, final QuestionnaireSet questionnaireSet) {

	final var optionalQuestionnaire = questionnaireRepository.findByQuestionnaireSetAndOrder(questionnaireSet, data.order());

	if (optionalQuestionnaire.isEmpty()) {

	    final var questionnaireTemp = new Questionnaire( //
			    data.name(), //
			    data.description(), //
			    data.order(), //
			    questionnaireSet, //
			    questionsFromFile //
	    );

	    return questionnaireRepository.save(questionnaireTemp);
	}

	final var questionnaire = optionalQuestionnaire.get();
	questionnaire.update(data.order(), data.name(), data.description(), questionnaireSet, questionsFromFile);
	return questionnaireRepository.update(questionnaire);
    }

    @Transactional(REQUIRED)
    public QuestionConcept readOrSaveQuestionConcept(final String questionConceptName, final Certification certification) {

	final var optionalQuestionConcept = questionConceptRepository.findByCertificationAndName(certification, questionConceptName);

	if (optionalQuestionConcept.isEmpty()) {
	    final var questionnaireSetTemp = new QuestionConcept(questionConceptName, certification);
	    return questionConceptRepository.save(questionnaireSetTemp);
	}

	return optionalQuestionConcept.get();
    }
    
    @Transactional(REQUIRED)
    public Question updateQuestion(final Question question) {
	return questionRepository.update(question);
    }

    public Stream<Questionnaire> findByQuestionnaireSet(final QuestionnaireSet questionnaireSet) {
	return questionnaireRepository.findByQuestionnaireSet(questionnaireSet);
    }

    public Optional<Questionnaire> findByQuestionnaireSetAndOrder(final QuestionnaireSet questionnaireSet, final Integer order) {

	if (ObjectUtils.anyNull(questionnaireSet, order)) {
	    return Optional.empty();
	}

	return questionnaireRepository.findByQuestionnaireSetAndOrder(questionnaireSet, order);
    }
    
    public Optional<Question> findById(final String id) {
	
	if (StringUtils.isBlank(id)) {
	    return Optional.empty();
	}
	
	return questionRepository.findById(id);
    }

    public Stream<Question> findByIds(final List<String> ids) {

	if (CollectionUtils.isEmpty(ids)) {
	    return Stream.<Question>of();
	}

	return questionRepository.findByIds(ids);
    }

    public Stream<QuestionDTO> findByCertificationAndQuestionnaireSetAndQuestionnaire(final Certification certification, final QuestionnaireSet questionnaireSet, final Questionnaire questionnaire) {

	if (ObjectUtils.anyNull(certification, questionnaireSet, questionnaire)) {
	    return Stream.of();
	}

	return questionRepository.findByCertificationAndQuestionnaireSetAndQuestionnaire(certification, questionnaireSet, questionnaire);
    }

    public Stream<QuestionDTO> findByCertificationAndQuestionnaireSet(final Certification certification, final QuestionnaireSet questionnaireSet) {

	if (ObjectUtils.anyNull(certification, questionnaireSet)) {
	    return Stream.of();
	}

	return questionRepository.findByCertificationAndQuestionnaireSet(certification, questionnaireSet);
    }

    public Stream<QuestionDTO> findByCertification(final Certification certification) {

	if (Objects.isNull(certification)) {
	    return Stream.of();
	}

	return questionRepository.findByCertification(certification);
    }

}
