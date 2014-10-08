package inspector.jmondb;

import inspector.jmondb.convert.Thermo.ThermoRawFileExtractor;
import inspector.jmondb.io.IMonDBManagerFactory;
import inspector.jmondb.io.IMonDBWriter;
import inspector.jmondb.model.Run;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManagerFactory;


public class CLI {

	protected static final Logger logger = LogManager.getLogger(CLI.class);

	public static void main(String[] args) {

		EntityManagerFactory emf = null;

		// create commandline options
		Options options = createOptions();

		// parse arguments
		CommandLineParser parser = new GnuParser();
		try {
			CommandLine cmd = parser.parse(options, args);

			// help
			if(cmd.hasOption("?"))
				new HelpFormatter().printHelp("jMonDB-core", options);
			else {
				boolean error = false;

				// database information
				String host = null;
				String port = null;
				String database = null;
				String user = null;
				String pass = null;
				if(cmd.hasOption("h"))
					host = cmd.getOptionValue("h");
				if(cmd.hasOption("p"))
					port = cmd.getOptionValue("p");
				if(cmd.hasOption("db"))
					database = cmd.getOptionValue("db");
				else {
					error = true;
					logger.error("No database provided");
					System.err.println("No database provided");
				}
				if(cmd.hasOption("u"))
					user = cmd.getOptionValue("u");
				else {
					error = true;
					logger.error("No user name provided");
					System.err.println("No user name provided");
				}
				if(cmd.hasOption("pw"))
					pass = cmd.getOptionValue("pw");

				// raw file information
				String rawFile = null;
				String instrumentName = null;
				if(cmd.hasOption("f"))
					rawFile = cmd.getOptionValue("f");
				else {
					error = true;
					logger.error("No raw file provided");
					System.err.println("No raw file provided");
				}
				if(cmd.hasOption("i"))
					instrumentName = cmd.getOptionValue("i");
				else {
					error = true;
					logger.error("No instrument name provided");
					System.err.println("No instrument name provided");
				}

				if(!error) {
					// create database connection
					emf = IMonDBManagerFactory.createMySQLFactory(host, port, database, user, pass);
					IMonDBWriter writer = new IMonDBWriter(emf);

					// store raw file in the database
					Run run = new ThermoRawFileExtractor().extractInstrumentData(rawFile, null, instrumentName);
					writer.writeRun(run);
				}
				else
					new HelpFormatter().printHelp("jMonDB-core", options);
			}

		} catch (ParseException e) {
			logger.error("Error while parsing the command-line arguments: {}", e.getMessage());
			System.err.println("Error while parsing the command-line arguments: " + e.getMessage());
			new HelpFormatter().printHelp("jMonDB-core", options);
		}
		finally {
			if(emf != null)
				emf.close();
		}
	}

	private static Options createOptions() {
		Options options = new Options();
		// help
		options.addOption("?", "help", false, "show help");
		// MySQL connection options
		options.addOption(new Option("h", "host", true, "the iMonDB MySQL host"));
		options.addOption(new Option("p", "port", true, "the iMonDB MySQL port"));
		options.addOption(new Option("db", "database", true, "the iMonDB MySQL database"));
		options.addOption(new Option("u", "user", true, "the iMonDB MySQL user name"));
		options.addOption(new Option("pw", "password", true, "the iMonDB MySQL password"));
		// raw file options
		options.addOption(new Option("f", "file", true, "the raw file to store in the iMonDB"));
		options.addOption(new Option("i", "instrument", true, "the name of the instrument on which the raw file was obtained (this instrument should be in the iMonDB already)"));

		return options;
	}
}
