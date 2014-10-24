package inspector.jmondb.model;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Represents an event that influenced the mass spectrometer, such as calibrations, maintenance, or unexpected incidents.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name="imon_event", uniqueConstraints=@UniqueConstraint(columnNames={"l_imon_instrument_id", "eventdate"}))
public class Event {

	@Transient
	private static final Logger logger = LogManager.getLogger(Event.class);

	/** read-only iMonDB primary key; generated by JPA */
	@Id
	@Column(name="id", nullable=false)
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;

	/** the {@link Instrument} on which the event occurred */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="l_imon_instrument_id", nullable=false, referencedColumnName="id")
	private Instrument instrument;

	/** the date on which the event occurred */
	@Column(name="eventdate", nullable=false)
	private Timestamp date;
	/** the {@link EventType} */
	@Column(name="type", nullable=false)
	private EventType type;
	/** a description of the observed problem */
	@Column(name="problem", columnDefinition="TEXT")
	private String problem;
	/** a description of the solution undertaken to solve the problem */
	@Column(name="solution", columnDefinition="TEXT")
	private String solution;
	/** additional custom information pertaining to the event */
	@Column(name="extra", columnDefinition="TEXT")
	private String extra;

	/** the attachment file name */
	@Column(name="attachment_name", length=255)
	private String attachmentFileName;
	/** the binary content of the attachment */
	@Lob
	@Column(name="attachment")
	private byte[] attachmentBytes;

	/**
	 * Default constructor required by JPA.
	 * Protected access modification enforces (limited) class immutability.
	 */
	protected Event() {

	}

	/**
	 * Creates an {@link Event}.
	 *
	 * @param instrument  the {@link Instrument} on which the event occurred
	 * @param date  the date on which the event occurred
	 * @param type  the {@link EventType}
	 */
	public Event(Instrument instrument, Timestamp date, EventType type) {
		setDate(date);
		setType(type);
		setInstrument(instrument);
	}

	/**
	 * Creates an {@link Event}.
	 *
	 * @param instrument  the {@link Instrument} on which the event occurred, not {@code null}
	 * @param date  the date on which the event occurred, not {@code null}
	 * @param type  the {@link EventType}, not {@code null}
	 * @param problem  a description of the observed problem
	 * @param solution  a description of the solution undertaken to solve the problem
	 * @param extra  additional custom information pertaining to the event
	 */
	public Event(Instrument instrument, Timestamp date, EventType type, String problem, String solution, String extra) {
		this(instrument, date, type);
		setProblem(problem);
		setSolution(solution);
		setExtra(extra);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	private void setInstrument(Instrument instrument) {
		if(instrument != null) {
			this.instrument = instrument;
			instrument.addEvent(this);
		}
		else {
			logger.error("The event's instrument is not allowed to be null");
			throw new NullPointerException("The event's instrument is not allowed to be null");
		}
	}

	public Timestamp getDate() {
		return date;
	}

	private void setDate(Timestamp date) {
		if(date != null)
			this.date = date;
		else {
			logger.error("The event's date is not allowed to be null");
			throw new NullPointerException("The event's date is not allowed to be null");
		}
	}

	public EventType getType() {
		return type;
	}

	private void setType(EventType type) {
		if(type != null)
			this.type = type;
		else {
			logger.error("The event's type is not allowed to be null");
			throw new NullPointerException("The event's type is not allowed to be null");
		}
	}

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getAttachmentName() {
		return attachmentFileName;
	}

	public byte[] getAttachmentContent() {
		return attachmentBytes;
	}

	public void setAttachmentName(String name) {
		attachmentFileName = name;
	}

	public void setAttachmentContent(byte[] content) {
		attachmentBytes = content;
	}

	public void setAttachment(File attachment) {
		// add the file content if the file is valid
		if(attachment != null) {
			try {
				setAttachmentName(attachment.getName());
				setAttachmentContent(FileUtils.readFileToByteArray(attachment));
			} catch(IOException e) {
				logger.warn("Unable to set file <{}> as an attachment", attachmentFileName);
				throw new IllegalArgumentException("Unable to set file <" + attachmentFileName +  "> as an attachment");
			}
		}
		else {
			// reset file content
			attachmentFileName = null;
			attachmentBytes = null;
		}
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Event event = (Event) o;

		if(!instrument.equals(event.instrument)) return false;
		if(!date.equals(event.date)) return false;
		if(type != event.type) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = instrument.hashCode();
		result = 31 * result + date.hashCode();
		result = 31 * result + type.hashCode();
		return result;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return "Event {id=" + id + ", instrument=" + instrument.getName() + ", date=" + sdf.format(date.getTime()) + ", type=" + type.toString() + "}";
	}
}
