package inspector.jmondb.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Entity
@Table(name="imon_property")
public class Property {

	@Transient
	private static final Logger logger = LogManager.getLogger(Property.class);

	/** read-only iMonDB primary key; generated by JPA */
	@Id
	@Column(name="id", nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	/** the name identifying the property */
	@Column(name="name", nullable=false, length=200)
	private String name;
	/** the type of property */
	@Column(name="type", nullable=false, length=20)
	private String type;

	/** list of {@link Value}s that are defined by the property */
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="hasProperty")
	@MapKey(name="id")
	private Map<Long, Value> definesValues;

	/** inverse part of the bi-directional relationship with {@link CvTerm} */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="l_imon_cv_term_id", referencedColumnName="id")
	private CvTerm hasCvTerm;

	/**
	 * Default constructor required by JPA.
	 * Protected access modification to enforce that client code uses the constructor that sets the required member variables.
	 */
	protected Property() {
		definesValues = new HashMap<>();
	}

	/**
	 * Creates a Property with the specified name and type.
	 *
	 * The id is automatically determined by the database as primary key.
	 *
	 * @param name  The name identifying the property
	 * @param type  The type of property
	 */
	public Property(String name, String type) {
		this();

		this.name = name;
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the number of {@link Value}s that are defined by the property.
	 *
	 * @return The number of Values that are defined by the property
	 */
	public int getNumberOfValues() {
		return definesValues.size();
	}

	/**
	 * Returns the {@link Value} with the specified id that is defined by the property.
	 *
	 * @param id  The id of the requested Value
	 * @return The Value with the specified id that is defined by the property
	 */
	public Value getValue(Long id) {
		if(id != null)
			return definesValues.get(id);
		else
			return null;
	}

	/**
	 * Returns an {@link Iterator} over all {@link Value}s that are defined by the property.
	 *
	 * @return An Iterator over all Values that are defined by the property
	 */
	public Iterator<Value> getValueIterator() {
		return definesValues.values().iterator();
	}

	/**
	 * Links the given {@link Value} to the property.
	 *
	 * If a Value with the same id was already present, the previous Value is replaced by the given Value.
	 *
	 * @param value  The Value that will be linked to the property
	 */
	public void addValue(Value value) {
		if(value != null) {
			value.setProperty(this);	// add the bi-directional relationship
			definesValues.put(value.getId(), value);
		}
		else {
			logger.error("Can't add <null> Value to a Property");
			throw new NullPointerException("Can't add <null> Value");
		}
	}

	/**
	 * Disconnects the {@link Value} specified by the given id from the property.
	 *
	 * @param id  The id of the Value that will be disconnected
	 */
	public void removeValue(Long id) {
		if(id != null) {
			Value value = definesValues.get(id);
			if(value != null)	// remove the bi-directional relationship
				value.setProperty(null);
			definesValues.remove(id);
		}
	}

	/**
	 * Disconnects all {@link Value}s from the property.
	 */
	public void removeAllValues() {
		Iterator<Value> it = getValueIterator();
		while(it.hasNext()) {
			Value value = it.next();
			// first remove the bi-directional relationship
			value.setProperty(null);
			// remove the value
			it.remove();
		}
	}

	public void setCvTerm(CvTerm term) {
		this.hasCvTerm = term;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Property that = (Property) o;

		if(id != that.id) return false;
		if(name != null ? !name.equals(that.name) : that.name != null) return false;
		if(type != null ? !type.equals(that.type) : that.type != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		return result;
	}
}