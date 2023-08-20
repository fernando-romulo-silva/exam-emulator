package org.examemulator.util.dto;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;
import static org.examemulator.util.domain.DomainUtil.MATH_CONTEXT;
import static org.examemulator.util.domain.DomainUtil.VALUE_100;

import java.math.BigDecimal;

public record QuestionDTO( //
		String questionnaireSetName, //
		String questionnaireName, //
		String idQuestion, //
		String value, //
		Integer questionOrder, //
		Integer qtyMarked, //
		Integer qtyCorrect, //
		BigDecimal percCorrect, //
		Integer qtyIncorrect, //
		BigDecimal percIncorrect, //
		Integer qtyTotal) {

    public QuestionDTO {
	qtyTotal = qtyCorrect + qtyIncorrect;

	percCorrect = qtyTotal > 0 ? new BigDecimal(qtyCorrect) //
			.divide(valueOf(qtyTotal), MATH_CONTEXT) //
			.multiply(VALUE_100) : ZERO;

	percIncorrect = qtyTotal > 0 ? new BigDecimal(qtyIncorrect) //
			.divide(valueOf(qtyTotal), MATH_CONTEXT) //
			.multiply(VALUE_100) : ZERO;
    }

    public QuestionDTO(//
		    String questionnaireSetName, //
		    String questionnaireName, //
		    String idQuestion, //
		    String value, //
		    Integer questionOrder, //
		    Integer qtyMarked, //
		    Integer qtyCorrect, //
		    Integer qtyIncorrect) {

	this(questionnaireSetName, questionnaireName, idQuestion, value, questionOrder, qtyMarked, qtyCorrect, ZERO, qtyIncorrect, ZERO, INTEGER_ZERO);
    }
}
