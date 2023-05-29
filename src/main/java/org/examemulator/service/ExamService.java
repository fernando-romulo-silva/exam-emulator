package org.examemulator.service;

import static java.util.stream.Collectors.joining;
import static org.examemulator.util.FileUtil.extractedExamName;
import static org.examemulator.util.FileUtil.readCorrectOptions;
import static org.examemulator.util.FileUtil.readExplanation;
import static org.examemulator.util.FileUtil.readOptions;
import static org.examemulator.util.FileUtil.readQuestion;
import static org.examemulator.util.FileUtil.readQuestionsFiles;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.exam.ExamOrigin;
import org.examemulator.domain.exam.Option;
import org.examemulator.domain.exam.Question;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExamService {

    private static AtomicLong idOptions = new AtomicLong(1);
    
    private static AtomicLong idQuestion = new AtomicLong(1);

    public Exam createExam( //
		    final String dir, //
		    final boolean practiceMode, //
		    final BigDecimal discretPercent, //
		    final BigDecimal minScorePercent, //
		    final boolean shuffleQuestions,
		    final boolean shuffleOptions) {

	final var questionFiles = readQuestionsFiles(dir);

	final var exam = new Exam.Builder().with($ -> {
	    $.name = extractedExamName(dir);
	    $.practiceMode = practiceMode;
	    $.discretPercent = discretPercent;
	    $.minScorePercent = minScorePercent;
	    $.randomOrder = false;
	    $.shuffleQuestions = shuffleQuestions;
	    $.shuffleOptions = shuffleOptions;
	    $.origin = ExamOrigin.FROM_PRETEST;
	}).build();

	for (final var questionFile : questionFiles) {

	    final var questionPath = Paths.get(dir + File.separator + questionFile);

	    try (final var lines = Files.lines(questionPath)) {

		exam.addQuestion(loadQuestion(questionFile, lines.collect(joining("\n")), true));
		
	    } catch (final IOException ex) {
		throw new IllegalStateException(ex);
	    }
	}

	return exam;
    }

    private Question loadQuestion(final String questionName, final String data, final boolean shuffleAnswers) {

	// read question number
	final var number = questionName.replaceAll("\\D", "");

	// read the question
	final var value = readQuestion(data);

	// read the correct answers
	final var correctOptions = readCorrectOptions(data);
	
	// read the options
	final var options = createOptions(data, correctOptions);
	
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

    private List<Option> createOptions(final String data, final List<String> correctOptions) {

	return readOptions(data).entrySet()//
			.stream() //
			.map(e -> new Option(idOptions.getAndIncrement(), e.getKey(), e.getValue(), correctOptions.contains(e.getKey()))) //
			.toList();
    }
}
