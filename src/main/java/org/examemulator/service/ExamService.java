package org.examemulator.service;

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
import java.nio.charset.CharacterCodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.examemulator.domain.Exam;
import org.examemulator.domain.Option;
import org.examemulator.domain.Question;

public class ExamService {

    public Exam createExam( //
		    final String dir, //
		    final boolean practiceMode, //
		    final BigDecimal discretPercent, //
		    final BigDecimal minScorePercent, //
		    final Map.Entry<Integer, Integer> range) {

	final var questionFiles = new ArrayList<String>();

	// load files
	try (final var stream = Files.list(Paths.get(dir))) {
	    questionFiles.addAll(stream.filter(file -> !Files.isDirectory(file)) //
			    .map(Path::getFileName) //
			    .map(Path::toString) //
			    .sorted((s1, s2) -> s1.compareTo(s2)) //
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
	}).build();

	final var questionFilesRanged = questionFiles.subList(range.getKey() - 1, range.getValue());

	for (final var questionFile : questionFilesRanged) {

	    final var questionPath = Paths.get(dir + File.separator + questionFile);

	    try (final var lines = Files.lines(questionPath)) {

		exam.addQuestion(loadQuestion(questionFile, lines.collect(joining("\n"))));
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

	final var folders = dir.split(File.separator);

	if (folders.length >= 2) {
	    final var last = ArrayUtils.subarray(folders, folders.length - 2, folders.length);
	    return Stream.of(last).collect(joining(" "));
	}

	return folders[folders.length - 1];
    }

    private Question loadQuestion(final String questionName, final String data) {

	// read question number
	final var number = questionName.replaceAll("[^0-9]", "");

	// read the question
	final var value = readQuestion(data);

	// read the options
	final var options = readOptions(data);

	// read the correct answers
	final var correctOptions = readCorrectOptions(data);

	// read the explanation
	final var explanation = readExplanation(data);

	return new Question.Builder().with($ -> {
	    $.id = questionName;
	    $.order = Integer.valueOf(number);
	    $.value = value;
	    $.options = options;
	    $.correctOptions = correctOptions;
	    $.explanation = explanation;
	    $.discrete = false;
	}).build();
    }

    private List<String> readCorrectOptions(final String data) {

	final var explanation = RegExUtils.removeAll(substringBetween(data, "Answer(s)", "\n"), "[,and\\s+]");

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

    private List<Option> readOptions(final String data) {

	final var dataTemp1 = replace(data, readQuestion(data), StringUtils.EMPTY);

	final var dataTemp2 = substringBefore(dataTemp1, "Answer(s)");

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
			.map(e -> new Option(e.getKey(), e.getValue())) //
			.toList();
    }

    private String readExplanation(final String data) {

	final var explanationTemp = substringAfter(data, "Answer(s)");

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
