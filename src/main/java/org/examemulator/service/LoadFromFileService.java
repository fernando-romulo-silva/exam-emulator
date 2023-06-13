package org.examemulator.service;

import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBetween;

import java.io.File;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.service.QuestionnaireService.ExamStructureFolder;
import org.jboss.weld.exceptions.IllegalArgumentException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LoadFromFileService {


    private final CertificationService certificationService;
    
    private final QuestionnaireService questionnaireService;
    
    @Inject
    LoadFromFileService(final CertificationService certificationService, final QuestionnaireService questionnaireService) {
	super();
	this.certificationService = certificationService;
	this.questionnaireService = questionnaireService;
    }


    public Questionnaire loadQuestionnaire(final String dir) {

	final var data = loadData(dir);

	final var certification = certificationService.loadCertification(data);

	final var questionnaireSet = questionnaireService.loadQuestionnaireSet(data, certification);

	final var questionnaire = questionnaireService.loadQuestionnaire(dir, data, questionnaireSet);

	return questionnaire;
    }
    
    private ExamStructureFolder loadData(final String dir) {

	final var splitedDir = StringUtils.split(dir, File.separator);

	if (StringUtils.isBlank(dir) || ArrayUtils.isEmpty(splitedDir) || splitedDir.length < 3) {
	    throw new IllegalArgumentException("Dirs are out of pattern, please check the documentation");
	}

	final var questionnaireTemp = splitedDir[splitedDir.length - 1];
	final var questionnaireName = substringBefore(questionnaireTemp, "(");
	final var questionnaireDesc = substringBetween(questionnaireTemp, "(", ")");

	final var setTemp = splitedDir[splitedDir.length - 2];
	final var setName = substringBefore(setTemp, "(");
	final var setDesc = substringBetween(setTemp, "(", ")");

	final var certificationName = substringBefore(splitedDir[splitedDir.length - 4], "(");

	return new ExamStructureFolder( //
			questionnaireName, //
			questionnaireDesc, setName, //
			setDesc, // 
			certificationName //
	);
    }
}
