package org.examemulator.service;

import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.QuestionnaireRepository;
import org.examemulator.domain.questionnaire.question.Question;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;
import org.examemulator.domain.questionnaire.set.QuestionnaireSetRespository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;

    private final QuestionnaireSetRespository questionnaireSetRespository;

    @Inject
    QuestionnaireService( //
		    final QuestionnaireRepository questionnaireRepository, //
		    final QuestionnaireSetRespository questionnaireSetRespository) {
	super();
	this.questionnaireRepository = questionnaireRepository;
	this.questionnaireSetRespository = questionnaireSetRespository;
    }

    public Questionnaire findPreExamByPreQuestion(final Question preQuestion) {

	return null;
    }

    @Transactional
    public Questionnaire loadQuestionnaire(final String dir, final ExamStructureFolder data, final QuestionnaireSet questionnaireSet) {

	final var optionalQuestionnaire = questionnaireRepository.findByNameAndCertification(data.questionnaireName, questionnaireSet.getCertification());

	if (optionalQuestionnaire.isEmpty()) {
	    final var questionnaireTemp = new Questionnaire(data.questionnaireName, data.examDesc, questionnaireSet);
//
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

    @Transactional
    public QuestionnaireSet loadQuestionnaireSet(final ExamStructureFolder data, final Certification certification) {

	final var optionalQuestionnaireSet = questionnaireSetRespository.findByNameAndCertification(data.setName, certification);

	if (optionalQuestionnaireSet.isEmpty()) {
	    final var questionnaireSetTemp = new QuestionnaireSet(data.setName, data.setDesc, certification);
	    return questionnaireSetRespository.save(questionnaireSetTemp);
	}

	return optionalQuestionnaireSet.get();
    }


    public record ExamStructureFolder(String questionnaireName, String examDesc, String setName, String setDesc, String certificationName) {
    }
}
