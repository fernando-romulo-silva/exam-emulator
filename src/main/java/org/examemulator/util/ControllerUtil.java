package org.examemulator.util;

import java.util.List;
import java.util.Optional;

public class ControllerUtil {

    public static <T> Optional<T> selectFirstQuestion(final List<T> questions) {

	if (questions.isEmpty()) {
	    return Optional.empty();
	}

	return Optional.of(questions.get(0));
    }
    
    public static <T> Optional<T> nextQuestion(final List<T> questions, final T selectedQuestion) {

	if (questions.isEmpty()) {
	    return Optional.empty();
	}

	final var currentIndex = questions.indexOf(selectedQuestion);
	final var iterator = questions.listIterator(currentIndex + 1);

	if (iterator.hasNext()) {
	    return Optional.of(iterator.next());
	}

	return Optional.empty();
    }

    public static <T> boolean hasNextQuestion(final List<T> questions, final T selectedQuestion) {
	if (questions.isEmpty()) {
	    return false;
	}

	final var currentIndex = questions.indexOf(selectedQuestion);
	if (currentIndex < 0) {
	    return false;
	}

	final var iterator = questions.listIterator(currentIndex + 1);
	return iterator.hasNext();
    }

    public static <T> boolean hasPreviousQuestion(final List<T> questions, final T selectedQuestion) {
	if (questions.isEmpty()) {
	    return false;
	}

	final var currentIndex = questions.indexOf(selectedQuestion);

	if (currentIndex < 0) {
	    return false;
	}

	final var iterator = questions.listIterator(currentIndex);
	return iterator.hasPrevious();
    }

    public static <T> Optional<T> previousQuestion(final List<T> questions, final T selectedQuestion) {

	if (questions.isEmpty()) {
	    return Optional.empty();
	}

	final var currentIndex = questions.indexOf(selectedQuestion);
	final var iterator = questions.listIterator(currentIndex);

	if (iterator.hasPrevious()) {
	    return Optional.of(iterator.previous());
	}

	return Optional.empty();
    }
}
