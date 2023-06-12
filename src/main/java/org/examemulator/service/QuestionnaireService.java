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
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.Option;
import org.examemulator.domain.questionnaire.Question;
import org.examemulator.domain.questionnaire.QuestionConcept;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.QuestionnaireRepository;
import org.examemulator.domain.questionnaire.QuestionnaireSet;
import org.examemulator.domain.questionnaire.QuestionnaireSetRespository;
import org.jboss.weld.exceptions.IllegalArgumentException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;

    private final QuestionnaireSetRespository questionnaireSetRespository;

    private final CertificationService certificationService;

    @Inject
    QuestionnaireService( //
		    final QuestionnaireRepository questionnaireRepository, //
		    final QuestionnaireSetRespository questionnaireSetRespository, //
		    final CertificationService certificationService) {
	super();
	this.questionnaireRepository = questionnaireRepository;
	this.questionnaireSetRespository = questionnaireSetRespository;
	this.certificationService = certificationService;
    }

    public Questionnaire findPreExamByPreQuestion(final Question preQuestion) {

	return null;
    }

    public Questionnaire loadQuestionnaire(final String dir) {

	final var data = loadData(dir);

	final var certification = certificationService.loadCertification(data);

	final var questionnaireSet = loadQuestionnaireSet(data, certification);

	final var questionnaire = loadQuestionnaire(dir, data, questionnaireSet);

	return questionnaire;
    }

    @Transactional
    public Questionnaire loadQuestionnaire(final String dir, final ExamStructureFolder data, final QuestionnaireSet questionnaireSet) {

	final var questionFiles = readQuestionsFiles(dir);

	final var optionalQuestionnaire = questionnaireRepository.findByNameAndCertification(data.setName, questionnaireSet.getCertification());

	if (optionalQuestionnaire.isEmpty()) {
	    final var questionnaireTemp = new Questionnaire(data.questionnaireName, data.examDesc, questionnaireSet);

	    final var conceptNames = questionFiles.stream() //
			    .filter(fn -> !containsNone(fn, '(', ')')) //
			    .map(fn -> substringBetween(fn, "(", ")")) //
			    .distinct() //
			    .map(fn -> new SimpleEntry<>(fn, new QuestionConcept(fn))) //
			    .collect(toMap(Entry::getKey, Entry::getValue));

	    for (final var questionFile : questionFiles) {

		final var questionPath = Paths.get(dir + File.separator + questionFile);

		try (final var lines = Files.lines(questionPath)) {

		    questionnaireTemp.addQuestion(loadQuestion(questionFile, lines.collect(joining("\n")), conceptNames));

		} catch (final IOException ex) {
		    throw new IllegalStateException(ex);
		}
	    }

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

    public List<QuestionConcept> loadQuestionConcepts(final String dir) {
	final var questionFiles = readQuestionsFiles(dir);
	return questionFiles.stream() //
			.filter(fn -> !containsNone(fn, '(', ')')) //
			.map(fn -> substringBetween(fn, "(", ")")) //
			.distinct() //
			.map(QuestionConcept::new) //
			.toList();
    }

    //

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

	final var certificationName = substringBefore(splitedDir[splitedDir.length - 3], "(");

	return new ExamStructureFolder( //
			questionnaireName, //
			questionnaireDesc, setName, //
			setDesc, certificationName //
	);
    }

    public record ExamStructureFolder(String questionnaireName, String examDesc, String setName, String setDesc, String certificationName) {
    }
}
