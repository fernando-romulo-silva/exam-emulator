package org.examemulator.infra.util.domain;

import static java.math.RoundingMode.HALF_UP;
import static org.examemulator.domain.exam.QuestionType.DISCRETE_MULTIPLE_CHOICE;
import static org.examemulator.domain.exam.QuestionType.DISCRETE_SINGLE_CHOICE;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

import org.examemulator.domain.exam.QuestionType;

public class DomainUtil {

    public static final List<QuestionType> DISCRET_LIST = List.of(DISCRETE_MULTIPLE_CHOICE, DISCRETE_SINGLE_CHOICE);

    public static final MathContext MATH_CONTEXT = new MathContext(2, HALF_UP); // 2 precision

    public static final BigDecimal VALUE_100 = BigDecimal.valueOf(100);

    public static final BigDecimal QUESTION_READY_VALUE = BigDecimal.valueOf(90);
    
    public static final Integer QUESTION_MIN_ATTEMPT = 5; 

    private DomainUtil() {
	throw new IllegalStateException("Utility class");
    }
}
