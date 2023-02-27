package org.examemulator.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "EXAM_GROUP", schema = "")
public class ExamGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    private Integer order;

    ExamGroup() {
	super();
    }

    public ExamGroup(final String name, final Integer order) {
	super();
	this.name = name;
	this.order = order;
    }
}
