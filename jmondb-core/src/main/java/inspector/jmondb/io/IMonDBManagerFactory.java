package inspector.jmondb.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates an {@link EntityManagerFactory} with specific properties to connect to a certain type of database.
 *
 * Remark: Don't forget to {@code close} the EntityManagerFactory after it has been fully used.
 */
public class IMonDBManagerFactory {

	private static final Logger logger = LogManager.getLogger(IMonDBManagerFactory.class);

	/**
	 * Creates an {@link EntityManagerFactory} for a MySQL database.
	 *
	 * @param host     the MySQL host, {@code localhost} if {@code null}
	 * @param port     the MySQL port, {@code 3306} if {@code null}
	 * @param db       the MySQL database schema, not {@code null}
	 * @param user     the MySQL user name, not {@code null}
	 * @param password the MySQL password, no password if {@code null}
	 * @return an {@code EntityManagerFactory} to be used to connect to the specified database
	 */
	public static EntityManagerFactory createMySQLFactory(String host, String port, String db, String user, String password) {
		if(db == null) {
			logger.error("Invalid database schema <null>");
			throw new NullPointerException("Invalid database schema");
		}
		if(user == null) {
			logger.error("Invalid database user <null>");
			throw new NullPointerException("Invalid database user");
		}

		logger.debug("Create MySQL EntityManagerFactory");

		// add connection-specific properties
		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
		host = host == null ? "localhost" : host;
		port = port == null ? "3306" : port;
		String url = "jdbc:mysql://" + host + ":" + port + "/" + db;
		properties.put("javax.persistence.jdbc.url", url);
		properties.put("javax.persistence.jdbc.user", user);
		if(password != null)
			properties.put("javax.persistence.jdbc.password", password);

		// create entity manager factory
		return Persistence.createEntityManagerFactory("jMonDB", properties);
	}
}
