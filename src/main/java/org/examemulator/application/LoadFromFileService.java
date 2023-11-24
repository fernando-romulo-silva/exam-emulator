package org.examemulator.application;

import static jakarta.transaction.Transactional.TxType.SUPPORTS;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.containsAny;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.examemulator.infra.util.FileUtil.readCorrectOptions;
import static org.examemulator.infra.util.FileUtil.readExplanation;
import static org.examemulator.infra.util.FileUtil.readOptions;
import static org.examemulator.infra.util.FileUtil.readQuestion;
import static org.examemulator.infra.util.FileUtil.readQuestionsFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.examemulator.application.QuestionnaireService.QuestionnaireDTO;
import org.examemulator.application.QuestionnaireSetService.QuestionnaireSetDTO;
import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.question.Option;
import org.examemulator.domain.questionnaire.question.Question;
import org.examemulator.domain.questionnaire.question.QuestionConcept;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional(SUPPORTS)
public class LoadFromFileService {

    private final CertificationService certificationService;

    private final QuestionnaireSetService questionnaireSetService;

    private final QuestionnaireService questionnaireService;

    private final String regexQuestionnaireSetName = "\\d{1,2} - [a-zA-Z0-9-_() ]+";
    
    private final String regexQuestionnaireName = "\\d{1,2} - Questionnaire \\d{1,2}[a-zA-Z0-9-_() ]+";

    @Inject
    LoadFromFileService( //
		    final CertificationService certificationService, //
		    final QuestionnaireSetService questionnaireSetService, //
		    final QuestionnaireService questionnaireService) {
	super();
	this.certificationService = certificationService;
	this.questionnaireSetService = questionnaireSetService;
	this.questionnaireService = questionnaireService;
    }

    // -------------------------------------------------------------------------------------------------------------------------------

    public Certification loadCertification(final Path certificationPath) {
	final var folderName = certificationPath.getFileName().toString();
	return certificationService.readOrSaveCertification(folderName);
    }

    public QuestionnaireSet loadQuestionnaireSet(final Path questionnaireSetPath, final Certification certification) {
	
	if (!questionnaireSetPath.getFileName().toString().matches(regexQuestionnaireSetName)) {

	}

	final var temp = questionnaireSetPath.getFileName().toString();
	final var order = substringBefore(temp, "-").trim();
	final var name = substringAfter(substringBefore(temp, "("), "-").trim();
	final var descTemp = substringBetween(temp, "(", ")");
	final var desc = StringUtils.isBlank(descTemp) ? null : descTemp.trim();

	final var data = new QuestionnaireSetDTO(NumberUtils.toInt(order), name, desc);

	return questionnaireSetService.readOrSaveQuestionnaireSet(data, certification);
    }

    public List<Question> loadQuestions(final Path questionnairePath, final Certification certification) {
	
	if (!questionnairePath.getFileName().toString().matches(regexQuestionnaireName)) {

	}
	
	final var questionFiles = readQuestionsFiles(questionnairePath);
	final var concepts = loadConcepts(questionFiles, certification);
	return loadQuestions(questionnairePath, questionFiles, concepts);
    }

    public Questionnaire loadQuestionnaire(final Path questionnairePath, final List<Question> questions, final QuestionnaireSet questionnaireSet) {

	final var temp = questionnairePath.getFileName().toString();
	final var order = substringBefore(temp, "-").trim();
	final var name = substringAfter(substringBefore(temp, "("), "-").trim();
	final var descTemp = substringBetween(temp, "(", ")");
	final var desc = StringUtils.isBlank(descTemp) ? null : descTemp.trim();

	final var data = new QuestionnaireDTO(NumberUtils.toInt(order), name, desc);

	return questionnaireService.saveOrUpdateQuestionnaire(data, questions, questionnaireSet);
    }

    // -------------------------------------------------------------------------------------------------------------------------------

    private Map<String, QuestionConcept> loadConcepts(final List<String> questionFiles, final Certification certification) {

	final var concepts = questionFiles.stream() //
			.filter(fn -> !StringUtils.containsNone(fn, '(', ')')) //
			.map(fn -> substringBetween(fn, "(", ")")) //
			.distinct() //
			.map(fn -> new SimpleEntry<>(fn, new QuestionConcept(fn, certification))) //
			.collect(toMap(Entry::getKey, Entry::getValue));

	final var result = new HashMap<String, QuestionConcept>();

	for (final var conceptEntry : concepts.entrySet()) {

	    final var conceptFileName = conceptEntry.getKey();
	    final var conceptFile = conceptEntry.getValue();

	    final var concept = questionnaireService.readOrSaveQuestionConcept(conceptFile.getName(), certification);

	    result.put(conceptFileName, concept);
	}

	return result;
    }

    private List<Question> loadQuestions(final Path dir, final List<String> questionFiles, final Map<String, QuestionConcept> conceptsMap) {

	final var questionsTemp = new ArrayList<Question>();

	for (final var questionFile : questionFiles) {

	    final var questionPath = Paths.get(dir + File.separator + questionFile);

	    try (final var lines = Files.lines(questionPath)) {

		questionsTemp.add(loadQuestion(questionFile, lines.collect(joining("\n")), conceptsMap));

	    } catch (final IOException ex) {
		throw new IllegalStateException(ex);
	    }
	}

	return questionsTemp;
    }

    private Question loadQuestion(final String questionFileName, final String data, final Map<String, QuestionConcept> conceptsMap) {

	final var questionName = containsAny(questionFileName, "(") ? substringBefore(questionFileName, "(") : substringBefore(questionFileName, ".txt");

	// read concepts
	final var conceptName = substringBetween(questionFileName, "(", ")");
	final var concept = Objects.nonNull(conceptName) //
			? conceptsMap.get(conceptName) //
			: null;

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
