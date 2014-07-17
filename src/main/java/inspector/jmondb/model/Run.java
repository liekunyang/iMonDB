package inspector.jmondb.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Entity
@Table(name="imon_run")
public class Run {

	@Transient
	private static final Logger logger = LogManager.getLogger(Run.class);

	/** read-only iMonDB primary key; generated by JPA */
	@Id
	@Column(name="id", nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	/** the name identifying the run */
	@Column(name="name", nullable=false, length=100)
	private String name;
	/** the location of the raw data belonging to the run */
	@Column(name="storage_name", nullable=false, length=255)
	private String storageName;
	/** the date on which the run was performed */
	@Column(name="sampledate", nullable=false)
	private Timestamp sampleDate;

	/** inverse part of the bi-directional relationship with {@link Project} */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="l_project_id", referencedColumnName="id")
	private Project fromProject;

	/** list of {@link Value}s for the run */
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="fromRun")
	@MapKey(name="id")
	private Map<Long, Value> hasValues;

	/**
	 * Default constructor required by JPA.
	 * Protected access modification to enforce that client code uses the constructor that sets the required member variables.
	 */
	protected Run() {
		hasValues = new HashMap<>();
	}

	/**
	 * Creates a Run with the specified name, storage name and sample date.
	 *
	 * The id is automatically determined by the database as primary key.
	 *
	 * @param name  The name identifying the run
	 * @param storageName  The location of the raw data belonging to the run
	 * @param sampleDate  The date on which the run was performed
	 */
	public Run(String name, String storageName, Timestamp sampleDate) {
		this();

		this.name = name;
		this.storageName = storageName;
		this.sampleDate = sampleDate;
	}

	//TODO: temporary copy constructor
	public Run(Run other) {
		this();

		setName(other.getName());
		setStorageName(other.getStorageName());
		setSampleDate(other.getSampleDate());

		for(Iterator<Value> it = other.getValueIterator(); it.hasNext(); )
			addValue(new Value(it.next()));
	}

	public long getId() {
		return id;
	}

	/* package private: read-only key to be set by the JPA implementation */
	void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStorageName() {
		return storageName;
	}

	public void setStorageName(String storageName) {
		this.storageName = storageName;
	}

	public Timestamp getSampleDate() {
		return sampleDate;
	}

	public void setSampleDate(Timestamp sampleDate) {
		this.sampleDate = sampleDate;
	}

	public void setFromProject(Project project) {
		this.fromProject = project;
	}

	/**
	 * Returns the number of {@link Value}s for the run.
	 *
	 * @return The number of Values for the run
	 */
	public int getNumberOfValues() {
		return hasValues.size();
	}

	/**
	 * Returns the {@link Value} with the specified id for the run.
	 *
	 * @param id  The id of the requested Value
	 * @return The Value with the specified id for the run
	 */
	public Value getValue(Long id) {
		if(id != null)
			return hasValues.get(id);
		else
			return null;
	}

	/**
	 * Returns an {@link Iterator} over all {@link Value}s for the run.
	 *
	 * @return An Iterator over all Values for the run
	 */
	public Iterator<Value> getValueIterator() {
		return hasValues.values().iterator();
	}

	/**
	 * Adds the given {@link Value} to the run.
	 *
	 * If a Value with the same id was already present, the previous Value is replaced by the given Value.
	 *
	 * @param value  The Value that will be added to the run
	 */
	public void addValue(Value value) {
		if(value != null) {
			value.setFromRun(this);	// add the bi-directional relationship
			hasValues.put(value.getId(), value);
		}
		else {
			logger.error("Can't add <null> Value to a Run");
			throw new NullPointerException("Can't add <null> Value");
		}
	}

	/**
	 * Removes the {@link Value} specified by the given id from the run.
	 *
	 * @param id  The id of the Value that will be removed
	 */
	public void removeValue(Long id) {
		if(id != null) {
			Value value = hasValues.get(id);
			if(value != null)	// remove the bi-directional relationship
				value.setFromRun(null);
			hasValues.remove(id);
		}
	}

	/**
	 * Removes all {@link Value}s from the run.
	 */
	public void removeAllValues() {
		Iterator<Value> it = getValueIterator();
		while(it.hasNext()) {
			Value value = it.next();
			// first remove the bi-directional relationship
			value.setFromRun(null);
			// remove the value
			it.remove();
		}
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Run that = (Run) o;

		if(id != that.id) return false;
		if(name != null ? !name.equals(that.name) : that.name != null) return false;
		if(sampleDate != null ? !sampleDate.equals(that.sampleDate) : that.sampleDate != null) return false;
		if(storageName != null ? !storageName.equals(that.storageName) : that.storageName != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (storageName != null ? storageName.hashCode() : 0);
		result = 31 * result + (sampleDate != null ? sampleDate.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Run {id=" + id + ", name=" + name + ", #values=" + getNumberOfValues() + "}";
	}
}