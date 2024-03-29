package org.examemulator.domain.cerfication;

import java.math.BigDecimal;
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
    private Integer maxMinutes = 60;
    
    @Column(name = "MIN_SCORE_PERCENT", precision = 19, scale = 2)
    private BigDecimal minScorePercent = new BigDecimal(70);

    // ------------------------------------------
    
    Certification() {
	super();
    }

    public Certification(final String name, final BigDecimal minScorePercent, final Integer maxMinutes) {
	super();
	this.name = name;
	this.minScorePercent = minScorePercent;
	this.maxMinutes = maxMinutes;
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
    
    public BigDecimal getMinScorePercent() {
        return minScorePercent;
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
