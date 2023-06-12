package org.examemulator.util.domain;

import static org.examemulator.domain.exam.QuestionType.DISCRETE_MULTIPLE_CHOICE;
import static org.examemulator.domain.exam.QuestionType.DISCRETE_SINGLE_CHOICE;

import java.util.List;

import org.examemulator.domain.exam.QuestionType;

public class DomainUtil {

    public static final List<QuestionType> DISCRET_LIST = List.of(DISCRETE_MULTIPLE_CHOICE, DISCRETE_SINGLE_CHOICE);
}
