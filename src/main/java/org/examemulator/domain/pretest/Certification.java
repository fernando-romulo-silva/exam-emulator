package org.examemulator.domain.pretest;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "CERTIFICATION")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", unique = true, nullable = false)
    private String name;
    
    @Column(name = "MAX_MINUTES")
    private Integer maxMinutes;
       
//    @JoinTable(name="BOOK_CHAPTER", joinColumns=@JoinColumn(name="BOOK_ID"))  
//    @ElementCollection(fetch=FetchType.EAGER)
//    @CollectionTable(name = "CERTIFICATION_CONCEPTS")
//    @MapKeyColumn(name = "KEY")
//    @Column(name = "VALUE")
//    private Map<Concept, BigDecimal> concepts;
    
    // ------------------------------------------
    Certification() {
	super();
    }

    public Certification(final String name) {
	super();
	this.name = name;
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

	} else if (obj instanceof Certification other) {
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
