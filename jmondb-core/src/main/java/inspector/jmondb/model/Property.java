package inspector.jmondb.model;

/*
 * #%L
 * jMonDB Core
 * %%
 * Copyright (C) 2014 InSPECtor
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
import org.hibernate.proxy.HibernateProxyHelper;

import javax.persistence.*;
import java.util.*;

/**
 * A {@code Property} provides a definition for specific {@link Value}s.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name="imon_property")
public class Property {

    @Transient
    private static final Logger LOGGER = LogManager.getLogger(Property.class);

    /** read-only iMonDB primary key; generated by JPA */
    @Id
    @Column(name="id", nullable=false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    /** the name of the property */
    @Column(name="name", nullable=false, length=200)
    private String name;
    /** the type of the property */
    @Column(name="type", nullable=false, length=20)
    private String type;
    /** the accession number that identifies the property in the controlled vocabulary */
    @Column(name="accession", nullable=false, unique=true, length=255)
    private String accession;
    /** the {@link CV} that contains the property definition */
    @ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.EAGER)
    @JoinColumn(name="l_imon_cv_id", nullable=false, referencedColumnName="id")
    private CV cv;

    /** indicates whether the property describes numerical data */
    @Column(name="isnumeric", nullable=false)
    private Boolean isNumeric;

    /** all {@link Value}s that are represented by this property */
    @OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="definingProperty")
    @MapKey(name="id")
    private Map<Run, Value> propertyValues;

    /** a sensible default capacity to reduce rehashing */
    private static final int DEFAULT_VALUE_CAPACITY = 512;

    /**
     * Default constructor required by JPA.
     * Protected access modification to enforce that client code uses the constructor that sets the required member variables.
     */
    protected Property() {
        propertyValues = new HashMap<>(DEFAULT_VALUE_CAPACITY);
    }

    /**
     * Creates a {@code Property} that defines some specific {@link Value}s.
     *
     * @param name  the name of the property, not {@code null}
     * @param type  the type of the property, not {@code null}
     * @param accession  the accession number that identifies the property in the controlled vocabulary, not {@code null}
     * @param cv  the {@link CV} that contains the property definition, not {@code null}
     * @param isNumeric  indicates whether the property describes numerical data, not {@code null}
     */
    public Property(String name, String type, String accession, CV cv, Boolean isNumeric) {
        this();

        setName(name);
        setType(type);
        setAccession(accession);
        setCv(cv);
        setNumeric(isNumeric);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        if(name != null) {
            this.name = name;
        } else {
            LOGGER.error("The property's name is not allowed to be <null>");
            throw new NullPointerException("The property's name is not allowed to be <null>");
        }
    }

    public String getType() {
        return type;
    }

    private void setType(String type) {
        if(type != null) {
            this.type = type;
        } else {
            LOGGER.error("The property's type is not allowed to be <null>");
            throw new NullPointerException("The property's type is not allowed to be <null>");
        }
    }

    public String getAccession() {
        return accession;
    }

    private void setAccession(String accession) {
        if(accession != null) {
            this.accession = accession;
        } else {
            LOGGER.error("The property's accession is not allowed to be <null>");
            throw new NullPointerException("The property's accession is not allowed to be <null>");
        }
    }

    public CV getCv() {
        return cv;
    }

    private void setCv(CV cv) {
        if(cv != null) {
            this.cv = cv;
        } else {
            LOGGER.error("The property's CV is not allowed to be <null>");
            throw new NullPointerException("The property's CV is not allowed to be <null>");
        }
    }

    public Boolean getNumeric() {
        return isNumeric;
    }

    private void setNumeric(Boolean numeric) {
        if(numeric != null) {
            this.isNumeric = numeric;
        } else {
            LOGGER.error("It is mandatory to specify whether the property is numeric");
            throw new NullPointerException("It is mandatory to specify whether the property is numeric");
        }
    }

    /**
     * Returns the {@link Value} that is defined by this {@code Property} and that originates from the given {@link Run}.
     *
     * @param run  the {@code Run} from which the requested {@code Value} originates, {@code null} returns {@code null}
     * @return the {@code Value} that is defined by this {@code Property} and that originates from the given {@code Run} if it exists, {@code null} otherwise
     */
    public Value getValue(Run run) {
        return run != null ? propertyValues.get(run) : null;
    }

    /**
     * Returns an {@link Iterator} over all {@link Value}s that are defined by this {@code Property}.
     *
     * @return an {@code Iterator} over all {@code Value}s that are defined by this {@code Property}
     */
    public Iterator<Value> getValueIterator() {
        return propertyValues.values().iterator();
    }

    /**
     * Assigns the given {@link Value} to this {@code Property}.
     *
     * If the {@code Property} previously contained a {@code Value} originating from the same {@link Run}, the old {@code Value} is replaced.
     *
     * A {@code Value} is automatically assigned to its {@code Property} upon its instantiation.
     *
     * @param value  the {@code Value} that is assigned to this {@code Property}, not {@code null}
     */
    void assignValue(Value value) {
        if(value != null) {
            propertyValues.put(value.getOriginatingRun(), value);
        } else {
            LOGGER.error("Can't add a <null> value to the property");
            throw new NullPointerException("Can't add a <null> value to the property");
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != HibernateProxyHelper.getClassWithoutInitializingProxy(o)) {
            return false;
        }

        final Property property = (Property) o;
        return 	   Objects.equals(name, property.getName())
                && Objects.equals(type, property.getType())
                && Objects.equals(accession, property.getAccession())
                && Objects.equals(cv, property.getCv())
                && Objects.equals(isNumeric, property.getNumeric());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, accession, cv, isNumeric);
    }

    @Override
    public String toString() {
        return "Property {id=" + id + ", name=" + name + ", type=" + type + ", CV=" + cv.getLabel()+ "#" + accession + "}";
    }
}
