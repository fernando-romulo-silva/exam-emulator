package org.examemulator.application;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.trim;

import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.examemulator.domain.cerfication.Certification;
import org.examemulator.domain.exam.Exam;
import org.examemulator.domain.exam.ExamRepository;
import org.examemulator.domain.questionnaire.Questionnaire;
import org.examemulator.domain.questionnaire.set.QuestionnaireSet;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional(SUPPORTS)
public class ExamService {

    private final ExamRepository examRepository;

    @Inject
    ExamService(final ExamRepository examRepository) {
	super();
	this.examRepository = examRepository;
    }

    @Transactional(REQUIRED)
    public void save(final Exam exam) {

	if (Objects.isNull(exam)) {
	    return;
	}

	if (Objects.isNull(exam.getId())) {
	    examRepository.save(exam);
	} else {
	    examRepository.update(exam);
	}
    }
    
    @Transactional(REQUIRED)
    public void delete(final Exam exam) {
	
	if (Objects.isNull(exam)) {
	    return;
	}
	
	examRepository.deleteExamQuestionsOptions(exam.getId());
	examRepository.deleteExamQuestions(exam.getId());
	examRepository.deleteExam(exam.getId());
	
//	examRepository.delete(exam);
    }

    public Stream<Exam> getAll() {
	return examRepository.findAll();
    }

    public Stream<Exam> findExamBy(final Certification selectedCertification) {
	return examRepository.findExamBy(selectedCertification);
    }
    
    public Stream<Exam> findExamBy(final Certification selectedCertification, final QuestionnaireSet questionnaireSet) {
	return examRepository.findExamBy(selectedCertification, questionnaireSet);
    }
    
    public Stream<Exam> findExamBy(final Certification selectedCertification, final QuestionnaireSet questionnaireSet, final Questionnaire questionnaire) {
	return examRepository.findExamBy(selectedCertification, questionnaireSet, questionnaire);
    }

    public String getExamNameBy(final Questionnaire questionnaire) {

	final var setName = questionnaire.getSet().getName();
	final String[] strings = { setName, " - ", questionnaire.getName(), " - ", "Exam" };
	final var name = StringUtils.join(strings);

	final var attempt = examRepository.getNextExamNumberBy(name);
	
	return name + SPACE + leftPad(attempt.toString(), 2, '0');
    }
    
    public String getExamNameByBy(final String name) {
	
	if (containsIgnoreCase(name, "retry")) {
	    
	    final var nameTemp = trim(substring(name, 0, name.length() - 2));
	    final var attempt = examRepository.getNextExamRetryNumberBy(nameTemp);
	    return nameTemp + SPACE + leftPad(attempt.toString(), 2, '0');
	    
	} else {
	    return name.concat(" - Retry 01");
	}
    }
    
    public String getNextExamDynamicNameBy(final Certification certification, final QuestionnaireSet questionnaireSet, final Questionnaire questionnaire) {

	final var preNameBuilder = new StringBuilder();
	
	if (ObjectUtils.allNotNull(certification, questionnaireSet, questionnaire)) {
	    
	    preNameBuilder.append(questionnaireSet.getName()).append(" - ").append(questionnaire.getName()).append(" - ");
	    
	} else if (ObjectUtils.allNotNull(certification, questionnaireSet)) {
	    
	    preNameBuilder.append(questionnaireSet.getName()).append(" - ");
	    
	} else if (Objects.nonNull(certification)) {
	    preNameBuilder.append(certification.getName()).append(" - ");
	} 
	
	final var preName = preNameBuilder.toString();
	
	final var name = preName.concat("Dynamic");
	
	final var attempt = examRepository.getNextExamNumberBy(name);
	
	return name + SPACE + leftPad(attempt.toString(), 2, '0');
    }    
}
