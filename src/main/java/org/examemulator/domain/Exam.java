package org.examemulator.domain;

import static java.math.RoundingMode.HALF_UP;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Optional.ofNullable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

import org.examemulator.util.RandomUtil;

public class Exam {

    private final String id; // uuid

    private final String name;

    private final BigDecimal minScorePercent;

    private final BigDecimal discretPercent;

    private final boolean randomOrder;

    private final boolean practiceMode;
    
    private final boolean shuffleOptions;
    
    private final boolean shuffleQuestions;

    private final List<Question> questions = new ArrayList<>();

    // -------------------------------

    private LocalDateTime start;

    private LocalDateTime finish;

    private ExamStatus status = ExamStatus.INITIAL;

    // -------------------------------

    private Exam(final Builder builder) {
	super();
	this.id = UUID.randomUUID().toString();
	this.name = builder.name;
	this.randomOrder = builder.randomOrder;
	this.practiceMode = builder.practiceMode;
	this.minScorePercent = builder.minScorePercent;
	this.discretPercent = builder.discretPercent;
	this.shuffleOptions = builder.shuffleOptions;
	this.shuffleQuestions = builder.shuffleQuestions;
    }

    public void addQuestion(final Question question) {

	if (status != ExamStatus.INITIAL) {
	    throw new IllegalStateException("You can add a new question only on Initial status!");
	}

	questions.add(question);
    }

    public void begin() {
	
	if (shuffleOptions) {
	    questions.forEach(Question::shuffleOptions);
	}
	
	if (shuffleQuestions) {
	    Collections.shuffle(questions, new Random(questions.size()));

	    int number = 1;

	    for (final var question : questions) {
		question.setOrder(number);
		number++;
	    }
	}

	if (status != ExamStatus.INITIAL) {
	    throw new IllegalStateException("You can begin a exam only on Initial status!");
	}

	if (questions.isEmpty()) {
	    throw new IllegalStateException("You can't begin a exam withou questions!");
	}

	if (discretPercent.intValue() > 0) {
	    final var discreteAvaliableList = questions.stream() //
			    .filter(question -> question.getOptions().size() > 2) //
			    .toList();

	    final var matchContext = new MathContext(3, HALF_UP); // 2 precision

	    final var perc = discretPercent.divide(BigDecimal.valueOf(100l)).round(matchContext).doubleValue();

	    final var discreteList = RandomUtil.getRandomSubList(discreteAvaliableList, perc);

	    for (final var question : discreteList) {
		question.defineToDiscrete(true);
	    }
	}

	start = LocalDateTime.now();
	status = ExamStatus.RUNNING;
    }

    public void finish() {

	if (status != ExamStatus.RUNNING) {
	    throw new IllegalStateException("You can finish a exam only on Running status!");
	}

	finish = LocalDateTime.now();
	status = ExamStatus.FINISHED;
    }

    // -----------------------------------------------------------------------------------------------------------------

    public String getId() {
	return id;
    }
    
    public String getName() {
	return name;
    }

    public BigDecimal getDiscretPercent() {
	return discretPercent;
    }

    public boolean isRandomOrder() {
	return randomOrder;
    }

    public boolean isPracticeMode() {
	return practiceMode;
    }

    public List<Question> getQuestions() {
	return questions;
    }

    public long getDuration() {

	if (status != ExamStatus.FINISHED) {
	    return 0;
	}

	return start.until(finish, MINUTES);
    }

    public LocalDateTime getFinish() {
	return finish;
    }

    public BigDecimal getMinScorePercent() {
	return minScorePercent;
    }

    public ExamStatus getStatus() {
	return status;
    }

    // -----------------------------------------------------------------------------------------------------------------

    public static final class Builder {

	public String name;

	public BigDecimal minScorePercent;

	public BigDecimal discretPercent;

	public boolean randomOrder;

	public boolean practiceMode;
	
	public boolean shuffleOptions = true;
	    
	public boolean shuffleQuestions = true;

	public Builder with(final Consumer<Builder> function) {
	    function.accept(this);
	    return this;
	}

	public Exam build() {
	    minScorePercent = ofNullable(minScorePercent).orElse(BigDecimal.valueOf(70));
	    discretPercent = ofNullable(discretPercent).orElse(BigDecimal.ZERO);
	    return new Exam(this);
	}
    }
}
