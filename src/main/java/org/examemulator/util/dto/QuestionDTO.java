package org.examemulator.util.dto;

import java.math.BigDecimal;

public record QuestionDTO( //
		Long id, //
		Integer order, //
		String name, //
		Integer execution, //
		BigDecimal perCorrect, //
		BigDecimal perIncorrect) {
}
