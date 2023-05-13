package org.examemulator.service;

import static java.io.File.separator;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.remove;
import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.commons.lang3.StringUtils.trim;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.exam.Option;
import org.examemulator.domain.exam.Question;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExamService {

    private static final String ANSWER_MSG = "Answer(s)";
    
    private static AtomicLong idOptions = new AtomicLong(1);
    
    private static AtomicLong idQuestion = new AtomicLong(1);

    public Exam createExam( //
		    final String dir, //
		    final boolean practiceMode, //
		    final BigDecimal discretPercent, //
		    final BigDecimal minScorePercent, //
		    final boolean shuffleQuestions,
		    final boolean shuffleOptions,
		    final Map.Entry<Integer, Integer> range) {

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

	final var exam = new Exam.Builder().with($ -> {
	    $.name = extractedExamName(dir);
	    $.practiceMode = practiceMode;
	    $.discretPercent = discretPercent;
	    $.minScorePercent = minScorePercent;
	    $.randomOrder = false;
	    $.shuffleQuestions = shuffleQuestions;
	    $.shuffleOptions = shuffleOptions;
	}).build();

	final var questionFilesRanged = questionFiles.subList(range.getKey() - 1, range.getValue());

	for (final var questionFile : questionFilesRanged) {

	    final var questionPath = Paths.get(dir + File.separator + questionFile);

	    try (final var lines = Files.lines(questionPath)) {

		exam.addQuestion(loadQuestion(questionFile, lines.collect(joining("\n")), true));
		
	    } catch (final IOException ex) {
		throw new IllegalStateException(ex);
	    }
	}

	return exam;
    }

    public int getQtyFiles(final String dir) {

	var result = 0;

	try (final var stream = Files.list(Paths.get(dir))) {
	    result = (int) stream.filter(file -> !Files.isDirectory(file)) //
			    .map(Path::getFileName) //
			    .count();

	} catch (final IOException ex) {
	    throw new IllegalStateException(ex);
	}

	return result;
    }

    public String extractedExamName(final String dir) {

	final var folders = dir.split(separator);

	if (folders.length >= 2) {
	    final var last = ArrayUtils.subarray(folders, folders.length - 2, folders.length);
	    return Stream.of(last).collect(joining(" "));
	}

	return folders[folders.length - 1];
    }

    private Question loadQuestion(final String questionName, final String data, final boolean shuffleAnswers) {

	// read question number
	final var number = questionName.replaceAll("\\D", "");

	// read the question
	final var value = readQuestion(data);

	// read the correct answers
	final var correctOptions = readCorrectOptions(data);
	
	// read the options
	final var options = readOptions(data, correctOptions);
	
	// read the explanation
	final var explanation = readExplanation(data);

	return new Question.Builder().with($ -> {
	    $.id = idQuestion.getAndIncrement();
	    $.name = questionName;
	    $.order = Integer.valueOf(number);
	    $.value = value;
	    $.options = options;
	    $.explanation = explanation;
	    $.discrete = false;
	}).build();
    }

    private List<String> readCorrectOptions(final String data) {

	final var explanation = RegExUtils.removeAll(substringBetween(data, ANSWER_MSG, "\n"), "[,and\\s+]");

	if (Objects.isNull(explanation)) {
	    throw new IllegalArgumentException("Error on read data: " + data);
	}

	return explanation //
			.chars() //
			.mapToObj(c -> EMPTY + (char) c) //
			.toList();
    }

    private String readQuestion(final String data) {

	final var dataTemp = StringUtils.removeEnd(StringUtils.removeEnd(data, "\n"), "\s");

	return trim(substringBefore(dataTemp, "A)"));
    }

    private List<Option> readOptions(final String data, final List<String> correctOptions) {

	final var dataTemp1 = replace(data, readQuestion(data), StringUtils.EMPTY);

	final var dataTemp2 = substringBefore(dataTemp1, ANSWER_MSG);

	final var answers = new HashMap<String, String>();

	final String[] possibleLetters = { "A)", "B)", "C)", "D)", "E)", "F)", "G)", "H)" };

	for (int j = 0; j < possibleLetters.length - 1; j++) {
	    var letter = possibleLetters[j];
	    var nextLetter = possibleLetters[j + 1];

	    if (contains(dataTemp2, letter) && contains(dataTemp2, nextLetter)) {

		answers.put(remove(letter, ')'), trim(substringBetween(dataTemp2, letter, nextLetter)));

	    } else if (contains(dataTemp2, letter)) {

		answers.put(remove(letter, ')'), trim(substringAfter(dataTemp2, letter)));
		break;

	    } else {
		break;
	    }
	}

	return answers.entrySet()//
			.stream() //
			.map(e -> new Option(idOptions.getAndIncrement(), e.getKey(), e.getValue(), correctOptions.contains(e.getKey()))) //
			.toList();
    }
    
    private String readExplanation(final String data) {

	final var explanationTemp = substringAfter(data, ANSWER_MSG);

	return substringAfter(explanationTemp, "\n");
    }

    private void checkIfUTF8(final String dir) {

	final var path = Paths.get(dir);
	
	try (final var reader = Files.newBufferedReader(path)) {
	    int c = reader.read();
	    
	    if (c == 0xfeff) {
		System.out.println("File starts with a byte order mark.");
	    } else if (c >= 0) {
		reader.transferTo(Writer.nullWriter());
	    }
	    
	} catch (final IOException ex) {
	    throw new IllegalArgumentException("Not a UTF-8 file", ex);
	}
    }
}
