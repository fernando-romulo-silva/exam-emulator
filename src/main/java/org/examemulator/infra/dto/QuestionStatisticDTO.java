package org.examemulator.infra.dto;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static org.examemulator.infra.util.DomainUtil.MATH_CONTEXT;
import static org.examemulator.infra.util.DomainUtil.VALUE_100;

import java.math.BigDecimal;

public record QuestionStatisticDTO( //
		Integer qtyQuestions,
		Integer qtyAnswers,
		Integer qtyMarked, //
		Integer qtyCorrect, //
		BigDecimal percCorrect, //
		Integer qtyIncorrect, //
		BigDecimal percIncorrect) {
    
    public QuestionStatisticDTO {

	percCorrect = qtyAnswers > 0 ? new BigDecimal(qtyCorrect) //
			.divide(valueOf(qtyAnswers), MATH_CONTEXT) //
			.multiply(VALUE_100) : ZERO;

	percIncorrect = qtyAnswers > 0 ? new BigDecimal(qtyIncorrect) //
			.divide(valueOf(qtyAnswers), MATH_CONTEXT) //
			.multiply(VALUE_100) : ZERO;
    }

    public QuestionStatisticDTO(//
		    Integer qtyQuestions, //
		    Integer qtyAnswers, //
		    Integer qtyMarked, //
		    Integer qtyCorrect, //
		    Integer qtyIncorrect) {

	this(qtyQuestions, qtyAnswers, qtyMarked, qtyCorrect, ZERO, qtyIncorrect, ZERO);
    }
}
