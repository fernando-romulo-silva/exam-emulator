package org.examemulator.domain.cerfication;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "CERTIFICATION", uniqueConstraints = { @UniqueConstraint(name = "CERTIFICATION_UC_NAME", columnNames = "NAME") })
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
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
    
    // ------------------------------------------

    public Long getId() {
	return id;
    }

    public String getName() {
	return name;
    }

    public Integer getMaxMinutes() {
	return maxMinutes;
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

	} else if (obj instanceof final Certification other) {
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
