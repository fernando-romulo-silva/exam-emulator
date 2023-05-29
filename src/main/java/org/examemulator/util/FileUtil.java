package org.examemulator.util;

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

import java.io.IOException;
import java.io.Writer;
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

public class FileUtil {

    public static final String ANSWER_MSG = "Answer(s)";

    public static final List<String> WORDS_ALL = List.of( //
		    "All of these", //
		    "All answers are true", //
		    "All answers are valid", //
		    "All of these are", //
		    "All of the options", //
		    "All of the preceding options", //
		    "All of the above", //
		    "All of the scenarios are valid", //
		    "All of the preceding", //
		    "All of the preceding options are true", //
		    "All of the preceding sentences are true", //
		    "All of the preceding steps are required", //
		    "All of the preceding answers are correct", //
		    "All of the preceding sentences are correct", //
		    "All of the preceding sentences are wrong", "All of the options" //
    );

    public static final List<String> WORDS_NONE = List.of(//
		    "None of these", //
		    "None of the options", //
		    "None of the options are valid", //
		    "None of the above", //
		    "None of the answers", //
		    "None of the answers is true", "None of the answers is correct", //
		    "None of the preceding options", //
		    "None of the preceding answers are true", "None of the preceding answers are correct", //
		    "None of the preceding sentences are right", //
		    "None of the preceding options are true" //
    );

    public static List<String> readQuestionsFiles(final String dir) {
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

	return questionFiles;
    }

    public static int getQtyFiles(final String dir) {

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

    public static String extractedExamName(final String dir) {

	final var folders = dir.split(separator);

	if (folders.length >= 2) {
	    final var last = ArrayUtils.subarray(folders, folders.length - 2, folders.length);
	    return Stream.of(last).collect(joining(" "));
	}

	return folders[folders.length - 1];
    }

    public static String readExplanation(final String data) {

	final var explanationTemp = substringAfter(data, ANSWER_MSG);

	return substringAfter(explanationTemp, "\n");
    }

    public static List<String> readCorrectOptions(final String data) {

	final var explanation = RegExUtils.removeAll(substringBetween(data, ANSWER_MSG, "\n"), "[,and\\s+]");

	if (Objects.isNull(explanation)) {
	    throw new IllegalArgumentException("Error on read data: " + data);
	}

	return explanation //
			.chars() //
			.mapToObj(c -> EMPTY + (char) c) //
			.toList();
    }

    public static String readQuestion(final String data) {

	final var dataTemp = StringUtils.removeEnd(StringUtils.removeEnd(data, "\n"), "\s");

	return trim(substringBefore(dataTemp, "A)"));
    }

    public static Map<String, String> readOptions(final String data) {
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

	return answers;
    }

    public static void checkIfUTF8(final String dir) {

	final var path = Paths.get(dir);

	try (final var reader = Files.newBufferedReader(path)) {
	    int c = reader.read();

	    if ((c != 0xfeff) && (c >= 0)) {
		reader.transferTo(Writer.nullWriter());
	    }

	} catch (final IOException ex) {
	    throw new IllegalArgumentException("Not a UTF-8 file", ex);
	}
    }
}
