package inspector.imondb.model;

/*
 * #%L
 * iMonDB Core
 * %%
 * Copyright (C) 2014 - 2015 InSPECtor
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.util.Objects;

/**
 * A {@code Value} signifies a summary value calculated out of a range of different observations.
 *
 * A {@code Value} can be uniquely identified by the combination of its defining {@link Property} and its originating {@link Run}.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "imon_value")
public class Value {

    @Transient
    private static final Logger LOGGER = LogManager.getLogger(Value.class);

    /** read-only iMonDB primary key; generated by JPA */
    @Id
    @Column(name="id", nullable=false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    /** the first observation */
    @Column(name="firstvalue", length=200)
    private String firstValue;
    /** the number of observations used to calculate the summary value */
    @Column(name="n")
    private Integer n;
    /** the number of different observations */
    @Column(name="n_diffvalues")
    private Integer nDiffValues;
    /** the minimum observation */
    @Column(name="min")
    private Double min;
    /** the maximum observation */
    @Column(name="max")
    private Double max;
    /** the mean observation */
    @Column(name="mean")
    private Double mean;
    /** the median observation */
    @Column(name="median")
    private Double median;
    /** the standard deviation */
    @Column(name="sd")
    private Double sd;
    /** the first quartile */
    @Column(name="q1")
    private Double q1;
    /** the third quartile */
    @Column(name="q3")
    private Double q3;

    /** inverse part of the bi-directional relationship with {@link Property} */
    @ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
    @JoinColumn(name="l_imon_property_id", nullable=false, referencedColumnName="id")
    private Property definingProperty;

    /** inverse part of the bi-directional relationship with {@link Run} */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="l_imon_run_id", nullable=false, referencedColumnName="id")
    private Run originatingRun;

    /**
     * Default constructor required by JPA.
     * Protected access modification enforces class immutability.
     */
    protected Value() {

    }

    /**
     * Creates a {@code Value}. Use the {@link ValueBuilder} to easily create a Value with a specific set of member variables.
     *
     * This {@code Value} signifies a summary value calculated out of a range of different observations.
     *
     * @param firstValue  the first observation
     * @param n  the number of observations used to calculate the summary value
     * @param nDiffValues  the number of different observations
     * @param min  the minimum observation
     * @param max  the maximum observation
     * @param mean  the mean observation
     * @param median  the median observation
     * @param sd  the standard deviation
     * @param q1  the first quartile
     * @param q3  the third quartile
     * @param property  the {@link Property} that defines the value, not {@code null}
     * @param run  the {@link Run} from which the value originates, not {@code null}
     */
    public Value(String firstValue, Integer n, Integer nDiffValues, Double min, Double max, Double mean, Double median, Double sd, Double q1, Double q3, Property property, Run run) {
        this();

        this.firstValue = firstValue;
        this.n = n;
        this.nDiffValues = nDiffValues;
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.median = median;
        this.sd = sd;
        this.q1 = q1;
        this.q3 = q3;

        setDefiningProperty(property);
        setOriginatingRun(run);
        property.assignValue(this);
        run.addValue(this);
    }

    public Long getId() {
        return id;
    }

    public Property getDefiningProperty() {
        return definingProperty;
    }

    private void setDefiningProperty(Property property) {
        if(property != null) {
            this.definingProperty = property;
        } else {
            LOGGER.error("The value's defining property is not allowed to be <null>");
            throw new NullPointerException("The value's defining property is not allowed to be <null>");
        }
    }

    public Run getOriginatingRun() {
        return originatingRun;
    }

    private void setOriginatingRun(Run run) {
        if(run != null) {
            this.originatingRun = run;
        } else {
            LOGGER.error("The value's originating run is not allowed to be <null>");
            throw new NullPointerException("The value's originating run is not allowed to be <null>");
        }
    }

    public String getFirstValue() {
        return firstValue;
    }

    public Integer getN() {
        return n;
    }

    public Integer getNDiffValues() {
        return nDiffValues;
    }

    public Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }

    public Double getMean() {
        return mean;
    }

    public Double getMedian() {
        return median;
    }

    public Double getSd() {
        return sd;
    }

    public Double getQ1() {
        return q1;
    }

    public Double getQ3() {
        return q3;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || !(o instanceof Value)) {
            return false;
        }

        final Value value = (Value) o;
        return  Objects.equals(this.firstValue, value.getFirstValue())
                && Objects.equals(this.n, value.getN())
                && Objects.equals(this.nDiffValues, value.getNDiffValues())
                && Objects.equals(this.min, value.getMin())
                && Objects.equals(this.max, value.getMax())
                && Objects.equals(this.mean, value.getMean())
                && Objects.equals(this.median, value.getMedian())
                && Objects.equals(this.sd, value.getSd())
                && Objects.equals(this.q1, value.getQ1())
                && Objects.equals(this.q3, value.getQ3())
                && Objects.equals(this.definingProperty, value.getDefiningProperty())
                && Objects.equals(this.originatingRun, value.getOriginatingRun());
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstValue, n, nDiffValues, min, max, mean, median, sd, q1, q3, definingProperty, originatingRun);
    }

    @Override
    public String toString() {
        return "Value {id=" + id + ", firstValue=" + firstValue +
                ", property=" + definingProperty.getName() + ", run=" + originatingRun.getName() + "}";
    }
}