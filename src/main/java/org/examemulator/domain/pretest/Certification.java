package org.examemulator.domain.pretest;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.MapKey;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "CERTIFICATION")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NAME")
    private String name;
    
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Column(name = "QTY_QUESTIONS")
    private Integer qtyQuestions;
    
    @JoinTable(name="BOOK_CHAPTER", joinColumns=@JoinColumn(name="BOOK_ID"))  
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name = "CERTIFICATION_CONCEPTS")
    @MapKeyColumn(name = "KEY")
    @Column(name = "VALUE")
    private Map<Concept, BigDecimal> concepts;
    
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

	} else if (obj instanceof Certification other) {
	    result = Objects.equals(id, other.id);

	} else {
	    result = false;
	}

	return result;
    }

    @Override
    public String toString() {
	final var sbToString = new StringBuilder(76);

	sbToString.append("Certification [id=").append(id) //
			.append(", name=").append(name) //
			.append(']');

	return sbToString.toString();
    }
}
