package inspector.jmondb.collect;

import inspector.jmondb.convert.Thermo.ThermoRawFileExtractor;
import inspector.jmondb.io.IMonDBManagerFactory;
import inspector.jmondb.io.IMonDBWriter;
import inspector.jmondb.model.Run;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;

import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class Collector {

	protected static final Logger logger = LogManager.getLogger(Collector.class);

	public Collector() {

		Ini config = initializeConfig();

		// create database connection
		EntityManagerFactory emf = IMonDBManagerFactory.createMySQLFactory(config.get("sql", "host"),
				config.get("sql", "port"), config.get("sql", "database"),
				config.get("sql", "user"), config.get("sql", "password"));

		try {
			IMonDBWriter dbWriter = new IMonDBWriter(emf);

			// read project folders
			Map<String, String> projects = config.get("projects");

			// browse all folders and find new raw files
			for(Map.Entry<String, String> entry : projects.entrySet()) {
				File baseDir = new File(entry.getValue());
				if(baseDir.isDirectory()) {
					// retrieve all files that were created after the specified date, and with file extension *.raw
					//TODO: fix AgeFileFilter
					//TODO: file mask to differentiate between BSA samples and experiment samples
					// create a custom filter that has to be overloaded?
					Collection<File> files = FileUtils.listFiles(baseDir,
							new AndFileFilter(new AgeFileFilter(new Date(2014, 1, 1)),
									new SuffixFileFilter(".raw", IOCase.INSENSITIVE)),
							DirectoryFileFilter.DIRECTORY);

					// process all found files
					for(File file : files) {
						ThermoRawFileExtractor extractor = new ThermoRawFileExtractor(file.getAbsolutePath());
						Run run = extractor.extractInstrumentData();

						// rename run based on the mask
						//TODO
						run.setName(entry.getKey() + "_" + FilenameUtils.getBaseName(file.getName()));

						// write the run to the database
						// TODO: verify if the run was already in the database?
						dbWriter.writeRun(run, entry.getKey());
					}
				} else {
					logger.error("Path <{}> is not a valid directory for project <{}>", entry.getValue(), entry.getKey());
					throw new IllegalArgumentException("Path <" + entry.getValue() + "> is not a valid directory");
				}
			}

			//TODO: write date to config file
		}
		finally {
			emf.close();
		}
	}

	private Ini initializeConfig() {
		try {
			// check whether the config file was specified as argument
			String systemConfig = System.getProperty("config.ini");
			if(systemConfig != null) {
				File configFile = new File(systemConfig);
				if(!configFile.exists()) {
					logger.error("The config file <{}> does not exist", systemConfig);
					throw new IllegalArgumentException("The config file to read does not exist: " + systemConfig);
				}
				else
					return new Ini(configFile);
			}

			// else load the config file
			return new Ini(Collector.class.getResourceAsStream("/config.ini"));

		} catch(IOException e) {
			logger.error("Error while reading the config file: {}", e);
			throw new IllegalStateException("Error while reading the exclusion properties: " + e);
		}
	}
}
