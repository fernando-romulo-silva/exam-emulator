package org.examemulator.infra.dto;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ZERO;
import static org.examemulator.infra.util.domain.DomainUtil.MATH_CONTEXT;
import static org.examemulator.infra.util.domain.DomainUtil.QUESTION_READY_VALUE;
import static org.examemulator.infra.util.domain.DomainUtil.VALUE_100;

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
		Integer qtyTotal,
		Boolean ready) {

    public QuestionDTO {
	qtyTotal = qtyCorrect + qtyIncorrect;

	percCorrect = qtyTotal > 0 ? new BigDecimal(qtyCorrect) //
			.divide(valueOf(qtyTotal), MATH_CONTEXT) //
			.multiply(VALUE_100) : ZERO;

	percIncorrect = qtyTotal > 0 ? new BigDecimal(qtyIncorrect) //
			.divide(valueOf(qtyTotal), MATH_CONTEXT) //
			.multiply(VALUE_100) : ZERO;
	
	if (percCorrect.compareTo(QUESTION_READY_VALUE) >= 0) {
	    ready = TRUE;
	}
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

	this(questionnaireSetName, questionnaireName, idQuestion, value, questionOrder, qtyMarked, qtyCorrect, ZERO, qtyIncorrect, ZERO, INTEGER_ZERO, FALSE);
    }
}
