package org.examemulator.service;

import java.math.BigDecimal;
import java.util.List;

import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.inquiry.InquiryInterface;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExamService {

    public Exam createExam( //
		    final List<? extends InquiryInterface> preQuestions, //
		    final boolean practiceMode, //
		    final BigDecimal discretPercent, //
		    final BigDecimal minScorePercent, //
		    final boolean shuffleQuestions, // 
		    final boolean shuffleOptions) {

	return new Exam.Builder().with($ -> {
	    $.practiceMode = practiceMode;
	    $.discretPercent = discretPercent;
	    $.minScorePercent = minScorePercent;
	    $.randomOrder = false;
	    $.shuffleQuestions = shuffleQuestions;
	    $.questions = preQuestions;
	}).build();
    }

}
