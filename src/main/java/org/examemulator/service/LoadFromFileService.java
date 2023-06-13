package org.examemulator.service;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.containsNone;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.examemulator.util.FileUtil.readCorrectOptions;
import static org.examemulator.util.FileUtil.readExplanation;
import static org.examemulator.util.FileUtil.readOptions;
import static org.examemulator.util.FileUtil.readQuestion;
import static org.examemulator.util.FileUtil.readQuestionsFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.question.Option;
import org.examemulator.domain.questionnaire.question.Question;
import org.examemulator.domain.questionnaire.question.QuestionConcept;
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

	final var questionFiles = readQuestionsFiles(dir);

	final var data = loadData(dir);

	final var certification = certificationService.loadCertification(data);
	
	final var concepts = loadConcepts(questionFiles, certification);
	
	final var questions = loadQuestions(dir, questionFiles, concepts);

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

    private Map<String, QuestionConcept> loadConcepts(final List<String> questionFiles, final Certification certification) {

	return questionFiles.stream() //
			.filter(fn -> !containsNone(fn, '(', ')')) //
			.map(fn -> substringBetween(fn, "(", ")")) //
			.distinct() //
			.map(fn -> new SimpleEntry<>(fn, new QuestionConcept(fn, certification))) //
			.collect(toMap(Entry::getKey, Entry::getValue));

    }

    private Map<String, Question> loadQuestions(final String dir, final List<String> questionFiles, final Map<String, QuestionConcept> conceptsMap) {

	final var questionsTemp = new HashedMap<String, Question>();

	for (final var questionFile : questionFiles) {

	    final var questionPath = Paths.get(dir + File.separator + questionFile);

	    try (final var lines = Files.lines(questionPath)) {

		questionsTemp.put(questionFile, loadQuestion(questionFile, lines.collect(joining("\n")), conceptsMap));

	    } catch (final IOException ex) {
		throw new IllegalStateException(ex);
	    }
	}

	return questionsTemp;
    }

    private Question loadQuestion(final String questionFileName, final String data, final Map<String, QuestionConcept> conceptsMap) {

	final var questionName = substringBefore(questionFileName, "(");

	// read concepts
	final var conceptName = substringBetween(questionFileName, "(", ")");
	final var concept = Objects.nonNull(conceptName) ? conceptsMap.get(conceptName) : null;

	// read question number
	final var number = questionName.replaceAll("\\D", "");

	// read the question
	final var value = readQuestion(data);

	// read the correct answers
	final var correctOptions = readCorrectOptions(data);

	// read the options
	final var options = createPreOptions(data, correctOptions);

	// read the explanation
	final var explanation = readExplanation(data);

	return new Question.Builder().with($ -> {
	    $.name = questionName;
	    $.order = Integer.valueOf(number);
	    $.value = value;
	    $.concept = concept;
	    $.options = options;
	    $.explanation = explanation;
	}).build();
    }

    private List<Option> createPreOptions(final String data, final List<String> correctOptions) {

	return readOptions(data).entrySet()//
			.stream() //
			.map(e -> new Option(e.getKey(), e.getValue(), correctOptions.contains(e.getKey()))) //
			.toList();
    }

}
