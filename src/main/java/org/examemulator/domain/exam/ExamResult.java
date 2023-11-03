package org.examemulator.domain.exam;

/**
 * Possible exam results
 * 
 * @author Fernando Romulo da Silva 
 */
public enum ExamResult {

    /**
     * Undefined because the exam are not finished.
     */
    UNDEFINED,

    /**
     * Below of minimum exam score and certification minimum score.  
     */
    FAILED,
    
    /**
     * Below of minimum exam score, although above or equal to certification minimum score.
     */
    ALMOST,

    /**
     * Above or equal of minimum exam and certification score.
     */
    PASSED
}
