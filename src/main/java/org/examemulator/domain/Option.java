package org.examemulator.domain;

import java.util.Objects;

public class Option {

    private String id;

    private String text;

    public Option(final String id, final String text) {
	super();
	this.id = id;
	this.text = text;
    }

    public String getId() {
	return id;
    }

    public String getText() {
	return text;
    }

    @Override
    public int hashCode() {
	return Objects.hash(id);
    }

    @Override
    public boolean equals(final Object obj) {

	final boolean result;

	if (this == obj) {
	    result = true;

	} else if (obj instanceof Option other) {
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
			.append(", text=").append(text).append(']');

	return sbToString.toString();
    }
}
