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
import java.util.UUID;
import java.util.function.Consumer;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", nullable = false)
    private final String id; // uuid

    @Column(name = "NAME")
    private final String name;

    @Column(name= "MIN_SCORE_PERCENT", precision = 19, scale = 2)
    private final BigDecimal minScorePercent;

    @Column(name= "DISCRETE_PERCENT", precision = 19, scale = 2)
    private final BigDecimal discretPercent;

    @Column(name = "RANDOM_ORDER")
    private final boolean randomOrder;

    @Column(name = "MODE")
    private final boolean practiceMode;
    
    @Column(name = "SHUFFLE_OPTIONS")
    private final boolean shuffleOptions;
    
    @Column(name = "SHUFFLE_QUESTIONS")
    private final boolean shuffleQuestions;
    
    @JoinColumn(name = "QUESTION_ID")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Question> questions = new ArrayList<>();
    
    @Column(name = "ORIGIN")
    @Enumerated(EnumType.STRING)
    private final ExamOrigin origin;

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
	this.origin = builder.origin;
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
	final var sbToString = new StringBuilder(76);

	sbToString.append("Option [id=").append(id) //
			.append(", name=").append(name) //
			.append(']');

	return sbToString.toString();
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
	
	public ExamOrigin origin = ExamOrigin.FROM_PRETEST;

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
