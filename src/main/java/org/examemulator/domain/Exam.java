package org.examemulator.domain;

import static java.math.RoundingMode.HALF_UP;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Optional.ofNullable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Exam {

    private final String id; // uuid

    private final String name;

    private final BigDecimal minScorePercent;

    private final BigDecimal discretPercent;

    private final boolean randomOrder;

    private final boolean practiceMode;

    private final List<Question> questions = new ArrayList<>();

    // -------------------------------

    private LocalDateTime start;

    private LocalDateTime finish;

    private ExamStatus status = ExamStatus.INITIAL;

    // -------------------------------

    public Exam(final String name) {
	super();
	this.id = UUID.randomUUID().toString();
	this.name = name;
	this.randomOrder = false;
	this.practiceMode = true;
	this.minScorePercent = BigDecimal.valueOf(70);
	this.discretPercent = BigDecimal.ZERO;
    }

    private Exam(final Builder builder) {
	super();
	this.id = UUID.randomUUID().toString();
	this.name = builder.name;
	this.randomOrder = builder.randomOrder;
	this.practiceMode = builder.practiceMode;
	this.minScorePercent = builder.minScorePercent;
	this.discretPercent = builder.discretPercent;
    }

    public void addQuestion(final Question question) {

	if (status != ExamStatus.INITIAL) {
	    throw new IllegalStateException("You can add a new question only on Initial status!");
	}

	questions.add(question);
    }

    public void begin() {

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

	    final var discreteList = DomainUtil.getRandomSubList(discreteAvaliableList, perc);

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
