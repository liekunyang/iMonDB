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
import java.util.Objects;

/**
 * Metadata describing additional information about a {@link Run}.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name="imon_metadata")
public class Metadata {

    @Transient
    private static final Logger LOGGER = LogManager.getLogger(Metadata.class);

    /** read-only iMonDB primary key; generated by JPA */
    @Id
    @Column(name="id", nullable=false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    /** the metadata name */
    @Column(name="name", nullable=false, length=100)
    private String name;
    /** the metadata value */
    @Column(name="value", nullable=false, length=100)
    private String value;

    /** inverse part of the bi-directional relationship with {@link Run} */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="l_imon_run_id", nullable=false, referencedColumnName="id")
    private Run run;

    /**
     * Default constructor required by JPA.
     * Protected access modification to enforce that client code uses the constructor that sets the required member variables.
     */
    protected Metadata() {

    }

    public Metadata(String name, String value, Run run) {
        this();

        setName(name);
        setValue(value);
        setRun(run);
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        if(name != null) {
            this.name = name;
        } else {
            LOGGER.error("The metadata name is not allowed to be <null>");
            throw new NullPointerException("The metadata name is not allowed to be <null>");
        }
    }

    public String getValue() {
        return value;
    }

    private void setValue(String value) {
        if(value != null) {
            this.value = value;
        } else {
            LOGGER.error("The metadata value is not allowed to be <null>");
            throw new NullPointerException("The metadata value is not allowed to be <null>");
        }
    }

    public Run getRun() {
        return run;
    }

    private void setRun(Run run) {
        if(run != null) {
            this.run = run;
            run.addMetadata(this);
        } else {
            LOGGER.error("The metadata run is not allowed to be <null>");
            throw new NullPointerException("The metadata run is not allowed to be <null>");
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

        final Metadata metadata = (Metadata) o;
        return     Objects.equals(name, metadata.getName())
                && Objects.equals(value, metadata.getValue())
                && Objects.equals(run, metadata.getRun());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, run);
    }

    @Override
    public String toString() {
        return "Metadata {id=" + id + ", " + name + "=" + value + ", run=" + run.getName() + '}';
    }
}
