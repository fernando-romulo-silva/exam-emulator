package org.examemulator.domain.questionnaire.set;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.examemulator.domain.cerfication.Certification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table( //
	name = "QUESTIONNARIE_SET", //
	uniqueConstraints = { //
		@UniqueConstraint(name = "QUESTIONNARIE_SET_UC_NAM_CERID", columnNames = { "NAME", "CERTIFICATION_ID" }), //
		@UniqueConstraint(name = "QUESTIONNARIE_SET_UC_NOR_CERID", columnNames = { "NUM_ORDER", "CERTIFICATION_ID" }) //
	} //
)
public class QuestionnaireSet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "NUM_ORDER")
    private Integer order;

    @ManyToOne
    @JoinColumn(name = "CERTIFICATION_ID", referencedColumnName = "ID", nullable = false)
    private Certification certification;

    QuestionnaireSet() {
	super();
    }

    public QuestionnaireSet(//
		    final String name, //
		    final String description, //
		    final Integer order, // 
		    final Certification certification) {
	super();
	this.name = name;
	this.description = description;
	this.order = order;
	this.certification = certification;
    }

    // ------------------------------------------
    
    public void update(final String name, final String description, final Integer order) {
	this.name = name;
	this.description = description;
	this.order = order;
    }
    // ------------------------------------------

    public Long getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public String getDescription() {
	return description;
    }
    
    public Integer getOrder() {
	return order;
    }

    public Certification getCertification() {
	return certification;
    }

    // ------------------------------------------

    @Override
    public int hashCode() {
	return Objects.hash(id);
    }

    @Override
    public boolean equals(final Object obj) {

	final boolean result;

	if (this == obj) {
	    result = true;

	} else if (obj instanceof QuestionnaireSet other) {
	    result = Objects.equals(id, other.id);

	} else {
	    result = false;
	}

	return result;
    }

    @Override
    public String toString() {
	return new ToStringBuilder(this)
			.append(id)
			.append(order)
			.append(name)
			.build();
    }
}
