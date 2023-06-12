package org.examemulator.domain.exam;

import static java.math.RoundingMode.HALF_UP;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Optional.ofNullable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.examemulator.domain.inquiry.InquiryInterface;
import org.examemulator.util.RandomUtil;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "EXAM")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "MIN_SCORE_PERCENT", precision = 19, scale = 2)
    private BigDecimal minScorePercent;

    @Column(name = "DISCRETE_PERCENT", precision = 19, scale = 2)
    private BigDecimal discretPercent;

    @Column(name = "RANDOM_ORDER")
    private boolean randomOrder;

    @Column(name = "MODE")
    private boolean practiceMode;

    @Column(name = "SHUFFLE_QUESTIONS")
    private boolean shuffleQuestions;

    @JoinColumn(name = "EXAM_QUESTION_ID")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ExamQuestion> questions = new ArrayList<>();

    @Column(name = "ORIGIN")
    @Enumerated(EnumType.STRING)
    private ExamOrigin origin;

    private LocalDateTime start;

    private LocalDateTime finish;

    private ExamStatus status = ExamStatus.INITIAL;

    // -----------------------------------------------------------------
    private Exam() {
	super();
    }
    
    private Exam(final Builder builder) {
	super();
	this.name = builder.name;
	this.randomOrder = builder.randomOrder;
	this.practiceMode = builder.practiceMode;
	this.minScorePercent = builder.minScorePercent;
	this.discretPercent = builder.discretPercent;
	this.shuffleQuestions = builder.shuffleQuestions;
	this.origin = builder.origin;
	this.questions.addAll(builder.questionsIntern);
    }

    // -----------------------------------------------------------------
    public void begin() {

	if (status != ExamStatus.INITIAL) {
	    throw new IllegalStateException("You can begin a exam only on Initial status!");
	}

	if (questions.isEmpty()) {
	    throw new IllegalStateException("You can't begin a exam withou questions!");
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

    public void pause() {

	if (status != ExamStatus.RUNNING) {
	    throw new IllegalStateException("You can pause a exam only on Running status!");
	}

	status = ExamStatus.PAUSED;
    }

    public void proceed() {

	if (status != ExamStatus.PAUSED) {
	    throw new IllegalStateException("You can proceed a exam only on Paused status!");
	}

	status = ExamStatus.RUNNING;
    }

    // -----------------------------------------------------------------------------------------------------------------

    public Long getId() {
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

    public List<ExamQuestion> getQuestions() {
	return Collections.unmodifiableList(questions);
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

    // -----------------------------------------------------------------------
    @Override
    public int hashCode() {
	return Objects.hash(id);
    }

    @Override
    public boolean equals(final Object obj) {

	final boolean result;

	if (this == obj) {
	    result = true;

	} else if (obj instanceof Exam other) {
	    result = Objects.equals(id, other.id);

	} else {
	    result = false;
	}

	return result;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this) //
			.append("id", id) //
			.append("name", name) //
			.append("practiceMode", practiceMode) //
			.toString();
    }

    // -----------------------------------------------------------------------------------------------------------------

    public static final class Builder {

	public String name;

	public BigDecimal minScorePercent;

	public BigDecimal discretPercent;

	public boolean randomOrder;

	public boolean practiceMode;

	public boolean shuffleQuestions = false;

	public boolean shuffleOptions = true;

	public List<? extends InquiryInterface> questions;

	private List<ExamQuestion> questionsIntern = new ArrayList<>();

	private ExamOrigin origin;

	public Builder with(final Consumer<Builder> function) {
	    function.accept(this);
	    return this;
	}

	public Exam build() {
	    minScorePercent = ofNullable(minScorePercent).orElse(BigDecimal.valueOf(70));
	    discretPercent = ofNullable(discretPercent).orElse(BigDecimal.ZERO);

	    if (shuffleQuestions) {
		Collections.shuffle(questions, new Random(questions.size()));
	    }

	    final List<? extends InquiryInterface> discretPercentList;

	    if (discretPercent.intValue() > 0) {
		final var discreteAvaliableList = questions.stream() //
				.filter(question -> question.getOptionsAmount() > 2) //
				.toList();

		final var matchContext = new MathContext(3, HALF_UP); // 2 precision

		final var perc = discretPercent.divide(BigDecimal.valueOf(100l)).round(matchContext).doubleValue();

		discretPercentList = RandomUtil.getRandomSubList(discreteAvaliableList, perc);
	    } else {
		discretPercentList = List.of();
	    }

	    int i = 1;

	    for (final var qTemp : questions) {

		final var number = Integer.valueOf(i);

		questionsIntern.add(new ExamQuestion.Builder() //
				.with(qTemp) //
				.with($ -> {
				    $.discrete = discretPercentList.contains(qTemp);
				    $.shuffleOptions = shuffleOptions;
				    $.order = number;
				}) //
				.build());

		i++;
	    }

//	    origin = findOrigin();
//	    
//	    name = switch (origin) {
//
//	    	case FROM_RETAKE -> "Retake from ";
//	    	case FROM_PRETEST -> "Exame from pre test";
//	    	
//	    	default -> throw new IllegalArgumentException("Unexpected value: ".concat(origin.toString()));
//	    };

	    return new Exam(this);
	}
    }
}
