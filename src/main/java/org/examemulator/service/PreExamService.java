package org.examemulator.service;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
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
import org.examemulator.domain.pretest.Certification;
import org.examemulator.domain.pretest.Concept;
import org.examemulator.domain.pretest.PreExam;
import org.examemulator.domain.pretest.PreGroup;
import org.examemulator.domain.pretest.PreOption;
import org.examemulator.domain.pretest.PreQuestion;
import org.jboss.weld.exceptions.IllegalArgumentException;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PreExamService {

    public PreExam findPreExamByPreQuestion(final PreQuestion preQuestion) {

	return null;
    }

    public PreExam loadPreExam(final String dir) {

	final var data = loadData(dir);

	final var questionFiles = readQuestionsFiles(dir);

	final var certification = new Certification(data.certificationName);

	final var pretestGroup = new PreGroup(data.groupName, data.groupDesc, certification);

	final var pretest = new PreExam(data.examName, data.examDesc, pretestGroup);

	final var conceptNames = questionFiles.stream() //
			.filter(fn -> !StringUtils.containsNone(fn, '(', ')')) //
			.map(fn -> substringBetween(fn, "(", ")")) //
			.distinct() //
			.map(fn -> new SimpleEntry<>(fn, new Concept(fn))) //
			.collect(toMap(Entry::getKey, Entry::getValue));

	for (final var questionFile : questionFiles) {

	    final var questionPath = Paths.get(dir + File.separator + questionFile);

	    try (final var lines = Files.lines(questionPath)) {

		pretest.addQuestion(loadQuestion(questionFile, lines.collect(joining("\n")), conceptNames));

	    } catch (final IOException ex) {
		throw new IllegalStateException(ex);
	    }
	}

	return pretest;
    }

    private PreQuestion loadQuestion(final String questionFileName, final String data, final Map<String, Concept> conceptsMap) {

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

	return new PreQuestion.Builder().with($ -> {
	    $.name = questionName;
	    $.order = Integer.valueOf(number);
	    $.value = value;
	    $.concept = concept;
	    $.options = options;
	    $.explanation = explanation;
	}).build();
    }

    private List<PreOption> createPreOptions(final String data, final List<String> correctOptions) {

	return readOptions(data).entrySet()//
			.stream() //
			.map(e -> new PreOption(e.getKey(), e.getValue(), correctOptions.contains(e.getKey()))) //
			.toList();
    }

    private ExamStructureFolder loadData(final String dir) {

	final var splitedDir = StringUtils.split(dir, File.separator);

	if (StringUtils.isBlank(dir) || ArrayUtils.isEmpty(splitedDir) || splitedDir.length < 3) {
	    throw new IllegalArgumentException("Dirs are out of pattern, please check the documentation");
	}

	final var examNameTemp = splitedDir[splitedDir.length - 1];
	final var examName = substringBefore(examNameTemp, "(");
	final var examDesc = substringBetween(examNameTemp, "(", ")");

	final var groupNameTemp = splitedDir[splitedDir.length - 2];
	final var groupName = substringBefore(groupNameTemp, "(");
	final var groupDesc = substringBetween(groupNameTemp, "(", ")");

	final var certificationName = substringBefore(splitedDir[splitedDir.length - 3], "(");

	return new ExamStructureFolder(examName, //
			examDesc,
			groupName, //
			groupDesc,
			certificationName //
	);
    }

    private record ExamStructureFolder(String examName, String examDesc, String groupName, String groupDesc, String certificationName) {
    }
}
