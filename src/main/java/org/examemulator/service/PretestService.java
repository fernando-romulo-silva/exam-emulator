package org.examemulator.service;

import static java.util.stream.Collectors.joining;
import static org.examemulator.util.FileUtil.extractedExamName;
import static org.examemulator.util.FileUtil.readCorrectOptions;
import static org.examemulator.util.FileUtil.readExplanation;
import static org.examemulator.util.FileUtil.readOptions;
import static org.examemulator.util.FileUtil.readQuestion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.examemulator.domain.pretest.PreExam;
import org.examemulator.domain.pretest.PreGroup;
import org.examemulator.domain.pretest.PreOption;
import org.examemulator.domain.pretest.PreQuestion;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PretestService {

    private static AtomicLong idOptions = new AtomicLong(1);

    private static AtomicLong idQuestion = new AtomicLong(1);

    public PreExam createExam(final String dir) {

	final var questionFiles = new ArrayList<String>();

	// load files
	try (final var stream = Files.list(Paths.get(dir))) {
	    questionFiles.addAll(stream.filter(file -> !Files.isDirectory(file)) //
			    .map(Path::getFileName) //
			    .map(Path::toString) //
			    .sorted(Comparable::compareTo) //
			    .toList());

	} catch (final IOException ex) {
	    throw new IllegalStateException(ex);
	}

	final var pretestGroup = new PreGroup(dir);

	final var pretest = new PreExam(extractedExamName(dir), pretestGroup);

	for (final var questionFile : questionFiles) {

	    final var questionPath = Paths.get(dir + File.separator + questionFile);

	    try (final var lines = Files.lines(questionPath)) {

		pretest.addQuestion(loadQuestion(questionFile, lines.collect(joining("\n"))));

	    } catch (final IOException ex) {
		throw new IllegalStateException(ex);
	    }
	}

	return pretest;
    }

    private PreQuestion loadQuestion(final String questionName, final String data) {

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
}
