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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.exam.Option;
import org.examemulator.domain.exam.Question;
import org.examemulator.domain.pretest.PreQuestion;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExamService {

    private static AtomicLong idOptions = new AtomicLong(1);

    private static AtomicLong idQuestion = new AtomicLong(1);

    public Exam createExam( //
		    final List<PreQuestion> preQuestions, //
		    final boolean practiceMode, //
		    final BigDecimal discretPercent, //
		    final BigDecimal minScorePercent, //
		    final boolean shuffleQuestions, final boolean shuffleOptions) {

	final var questions = new ArrayList<Question>();
	
	for (final var preQuestion : preQuestions) {
	    final var questionsTemp = preQuestion.getOptions().stream() //
			    .map(pQuestion -> new Option( //
					    idOptions.getAndIncrement(), //
					    pQuestion.getLetter(), //
					    pQuestion.getValue(), //
					    pQuestion.isCorrect()) //
			    ).toList();

	    questions.add(new Question.Builder().with($ -> {
		$.id = idQuestion.getAndIncrement();
		$.name = preQuestion.getName();
		$.order = preQuestion.getOrder();
		$.value = preQuestion.getValue();
		$.options = questionsTemp;
		$.explanation = preQuestion.getExplanation();
		$.discrete = false;
	    }).build());
	}

	return new Exam.Builder().with($ -> {
	    $.practiceMode = practiceMode;
	    $.discretPercent = discretPercent;
	    $.minScorePercent = minScorePercent;
	    $.randomOrder = false;
	    $.shuffleQuestions = shuffleQuestions;
	    $.shuffleOptions = shuffleOptions;
	    $.questions = questions;
	}).build();
    }

}
