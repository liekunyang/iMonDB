package inspector.jmondb.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * A {code Run} represents a single experimental run (signified by a single raw file), and can contain several {@link Value}s.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name="imon_run", uniqueConstraints=@UniqueConstraint(columnNames={"l_imon_instrument_id", "name"}))
public class Run {

	@Transient
	private static final Logger logger = LogManager.getLogger(Run.class);

	/** read-only iMonDB primary key; generated by JPA */
	@Id
	@Column(name="id", nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	/** the name identifying the run */
	@Column(name="name", nullable=false, length=100)
	private String name;
	/** the location of the raw data belonging to the run */
	@Column(name="storage_name", nullable=false, length=255)
	private String storageName;
	/** the date on which the run was performed */
	@Column(name="sampledate", nullable=false)
	private Timestamp sampleDate;

	/** additional {@link Metadata} describing the run */
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="run")
	@MapKey(name="name")
	private Map<String, Metadata> metadata;

	/** all {@link Value}s for the run */
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="originatingRun")
	@MapKeyJoinColumn(name="l_imon_property_id", referencedColumnName="id")
	@MapKeyClass(Property.class)
	private Map<Property, Value> runValues;

	/** inverse part of the bi-directional relationship with {@link Instrument} */
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch=FetchType.LAZY)
	@JoinColumn(name="l_imon_instrument_id", nullable=false, referencedColumnName="id")
	private Instrument instrument;

	/**
	 * Default constructor required by JPA.
	 * Protected access modification to enforce that client code uses the constructor that sets the required member variables.
	 */
	protected Run() {
		metadata = new HashMap<>(10);
		runValues = new HashMap<>(250);
	}

	/**
	 * Creates a {@code Run} representing a specific experiment.
	 *
	 * @param name  the name identifying the run, not {@code null}
	 * @param storageName  the location of the raw data belonging to the run, not {@code null}
	 * @param sampleDate  the date on which the run was performed, not {@code null}
	 * @param instrument  the {@link Instrument} on which the run was executed, not {@code null}
	 */
	public Run(String name, String storageName, Timestamp sampleDate, Instrument instrument) {
		this();

		setName(name);
		setStorageName(storageName);
		setSampleDate(sampleDate);
		setInstrument(instrument);
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		if(name != null)
			this.name = name;
		else {
			logger.error("The run's name is not allowed to be <null>");
			throw new NullPointerException("The run's name is not allowed to be <null>");
		}
	}

	public String getStorageName() {
		return storageName;
	}

	private void setStorageName(String storageName) {
		if(storageName != null)
			this.storageName = storageName;
		else {
			logger.error("The run's storage name is not allowed to be <null>");
			throw new NullPointerException("The run's storage name is not allowed to be <null>");
		}
	}

	public Timestamp getSampleDate() {
		return sampleDate;
	}

	private void setSampleDate(Timestamp sampleDate) {
		if(sampleDate != null)
			this.sampleDate = sampleDate;
		else {
			logger.error("The run's sample date is not allowed to be <null>");
			throw new NullPointerException("The run's sample date is not allowed to be <null>");
		}
	}

	public Instrument getInstrument() {
		return instrument;
	}

	private void setInstrument(Instrument instrument) {
		if(instrument != null) {
			this.instrument = instrument;
			instrument.addRun(this);
		}
		else {
			logger.error("The run's instrument is not allowed to be <null>");
			throw new NullPointerException("The run's instrument is not allowed to be <null>");
		}
	}

	/**
	 * Returns the {@link Value} that originates from this {@code Run} and that is defined by the given {@link Property}.
	 *
	 * @param property  the {@code Property} that defines the requested {@code Value}, {@code null} returns {@code null}
	 * @return the {@code Value} that originates from this {@code Run} and that is defined by the given {@link Property} if it exists, {@code null} otherwise
	 */
	public Value getValue(Property property) {
		if(property != null)
			return runValues.get(property);
		else
			return null;
	}

	/**
	 * Returns an {@link Iterator} over all {@link Value}s that originate from this {@code Run}.
	 *
	 * @return an {@code Iterator} over all {@code Value}s that originate from this {@code Run}
	 */
	public Iterator<Value> getValueIterator() {
		return runValues.values().iterator();
	}

	/**
	 * Adds the given {@link Value} to this {@code Run}.
	 *
	 * If the {@code Run} previously contained a {@code Value} associated with the same {@link Property}, the old {@code Value} is replaced.
	 *
	 * A {@code Value} is automatically added to its {@code Run} upon its instantiation.
	 *
	 * @param value  the {@code Value} that is added to this {@code Run}, not {@code null}
	 */
	void addValue(Value value) {
		if(value != null) {
			// add the value to the run
			runValues.put(value.getDefiningProperty(), value);
			// add the value's defining property to the instrument
			instrument.assignProperty(value.getDefiningProperty());
		}
		else {
			logger.error("Can't add a <null> value to the run");
			throw new NullPointerException("Can't add a <null> value to the run");
		}
	}

	/**
	 * Returns the {@link Metadata} for this {@code Run} with the given name.
	 *
	 * @param name  the name of the requested {@code Metadata}, {@code null} returns {@code null}
	 * @return the {@code Metadata} for this {@code Run} with the given name if it exists, {@code null} otherwise
	 */
	public Metadata getMetadata(String name) {
		if(name != null)
			return metadata.get(name);
		else
			return null;
	}

	/**
	 * Returns an {@link Iterator} over all {@link Metadata} for this {@code Run}.
	 *
	 * @return an {@code Iterator} over all {@code Metadata} for this {@code Run}
	 */
	public Iterator<Metadata> getMetadataIterator() {
		return metadata.values().iterator();
	}

	/**
	 * Assigns the given {@link Metadata} to this {@code Run}.
	 *
	 * If the {@code Run} previously contained a {@code Metadata} with the same name, the old {@code Metadata} is replaced.
	 *
	 * {@code Metadata} is automatically added to its {@code Run} upon its instantiation.
	 *
	 * @param meta  the {@code Metadata} that is assigned to this {@code Run}, not {@code null}
	 */
	void addMetadata(Metadata meta) {
		if(meta != null)
			metadata.put(meta.getName(), meta);
		else {
			logger.error("Can't add <null> metadata to the run");
			throw new NullPointerException("Can't add <null> metadata to the run");
		}
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Run run = (Run) o;

		if(!name.equals(run.name)) return false;
		if(!storageName.equals(run.storageName)) return false;
		if(!sampleDate.equals(run.sampleDate)) return false;
		if(!instrument.getName().equals(run.instrument.getName())) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + storageName.hashCode();
		result = 31 * result + sampleDate.hashCode();
		result = 31 * result + instrument.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Run {id=" + id + ", name=" + name + ", instrument=" + instrument.getName() + ", #values=" + runValues.size() + "}";
	}
}
