package org.examemulator.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "EXAM_GROUP")
public class ExamGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name") 
    private String name;

    @Column(name = "seq")
    private Integer seq;

    ExamGroup() {
	super();
    }

    public ExamGroup(final String name, final Integer seq) {
	super();
	this.name = name;
	this.seq = seq;
    }
}
