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
import org.examemulator.domain.questionnaire.set.QuestionnaireSetRespository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional(value = SUPPORTS)
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;

    private final QuestionnaireSetRespository questionnaireSetRespository;

    private final QuestionConceptRepository questionConceptRepository;

    @Inject
    QuestionnaireService( //
		    final QuestionnaireRepository questionnaireRepository, //
		    final QuestionnaireSetRespository questionnaireSetRespository, //
		    final QuestionConceptRepository questionConceptRepository) {
	super();
	this.questionnaireRepository = questionnaireRepository;
	this.questionnaireSetRespository = questionnaireSetRespository;
	this.questionConceptRepository = questionConceptRepository;
    }

    @Transactional(value = REQUIRED)
    public Questionnaire saveOrUpdateQuestionnaire(//
		    final ExamStructureFolder data, // 
		    final QuestionnaireSet questionnaireSet, // 
		    final List<Question> questionsFromFile) {

	final var optionalQuestionnaire = questionnaireRepository.findByNameAndCertification(data.questionnaireName(), questionnaireSet.getCertification());

	if (optionalQuestionnaire.isEmpty()) {
	    final var questionnaireTemp = new Questionnaire(data.questionnaireName(), data.questionnaireDesc(), data.questionnaireOrder(), questionnaireSet, questionsFromFile);

//	    final var conceptsMap = questionFiles.stream() //
//			    .filter(fn -> !containsNone(fn, '(', ')')) //
//			    .map(fn -> substringBetween(fn, "(", ")")) //
//			    .distinct() //
//			    .map(fn -> new SimpleEntry<>(fn, new QuestionConcept(fn))) //
//			    .collect(toMap(Entry::getKey, Entry::getValue));
//
//	    for (final var questionFile : questionFiles) {
//
//		final var questionPath = Paths.get(dir + File.separator + questionFile);
//
//		try (final var lines = Files.lines(questionPath)) {
//
//		    questionnaireTemp.addQuestion(loadQuestion(questionFile, lines.collect(joining("\n")), conceptsMap));
//
//		} catch (final IOException ex) {
//		    throw new IllegalStateException(ex);
//		}
//	    }

	    return questionnaireRepository.save(questionnaireTemp);
	}

	return optionalQuestionnaire.get();
    }

    @Transactional(value = REQUIRED)
    public QuestionnaireSet readOrSaveQuestionnaireSet(final ExamStructureFolder data, final Certification certification) {

	final var optionalQuestionnaireSet = questionnaireSetRespository.findByNameAndCertification(data.setName(), certification);

	if (optionalQuestionnaireSet.isEmpty()) {
	    final var questionnaireSetTemp = new QuestionnaireSet(data.setName(), data.setDesc(), data.setOrder(), certification);
	    return questionnaireSetRespository.save(questionnaireSetTemp);
	}

	return optionalQuestionnaireSet.get();
    }

    @Transactional(value = REQUIRED)
    public QuestionConcept readOrSaveQuestionConcept(final String questionConceptName, final Certification certification) {

	final var optionalQuestionConcept = questionConceptRepository.findByNameAndCertification(questionConceptName, certification);

	if (optionalQuestionConcept.isEmpty()) {
	    final var questionnaireSetTemp = new QuestionConcept(questionConceptName, certification);
	    return questionConceptRepository.save(questionnaireSetTemp);
	}

	return optionalQuestionConcept.get();
    }
    
    public Stream<Questionnaire> findByCertificationAndQuestionnaireSet(final Certification certification,  final QuestionnaireSet questionnaireSet) {
	return questionnaireRepository.findByCertificationAndQuestionnaireSet(certification, questionnaireSet);
    }
}
