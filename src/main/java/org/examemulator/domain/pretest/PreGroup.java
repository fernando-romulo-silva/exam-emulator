package org.examemulator.domain.pretest;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
    
    @OneToOne
    @JoinColumn(name = "CERTIFICATION_ID", referencedColumnName = "ID")
    private Certification certification;

    PreGroup() {
	super();
    }

    public PreGroup(final String name) {
	super();
	this.name = name;
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
	final var sbToString = new StringBuilder(76);

	sbToString.append("PretestGroup [id=").append(id) //
			.append(", name=").append(name) //
			.append(']');

	return sbToString.toString();
    }
}
