package org.examemulator.domain.pretest;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "GROUP")
public class PreGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME")
    private String name;
    
    @Column(name = "Description")
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "CERTIFICATION_ID", referencedColumnName = "ID", nullable = false)
    private Certification certification;
    
    PreGroup() {
	super();
    }

    public PreGroup(final String name, final String description, final Certification certification) {
	super();
	this.name = name;
	this.description = description;
	this.certification = certification;
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

	} else if (obj instanceof PreGroup other) {
	    result = Objects.equals(id, other.id);

	} else {
	    result = false;
	}

	return result;
    }

    @Override
    public String toString() {
	return ToStringBuilder.reflectionToString(this);
    }
}
