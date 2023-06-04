package org.examemulator.domain.inquiry;

public sealed interface InquiryInterface permits PreQuestion, Question {

    default int getOptionsAmount() {
	return 0;
    }
}
