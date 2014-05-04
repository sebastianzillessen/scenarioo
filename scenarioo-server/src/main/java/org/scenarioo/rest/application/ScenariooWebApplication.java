/* scenarioo-server
 * Copyright (C) 2014, scenarioo.org Development Team
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.scenarioo.rest.application;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.scenarioo.business.builds.ScenarioDocuBuildsManager;
import org.scenarioo.dao.configuration.ConfigurationDAO;
import org.scenarioo.model.configuration.Configuration;

/**
 * Scenarioo REST Services Web Application context that initializes data on
 * startup of server.
 */
public class ScenariooWebApplication implements ServletContextListener {

	private static final Logger LOGGER = Logger
			.getLogger(ScenariooWebApplication.class);

	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		LOGGER.info("====================================================");
		LOGGER.info("Scenarioo webapplication server is starting up ...  ");
		LOGGER.info("====================================================");

		initializeApplicationVersion(servletContextEvent.getServletContext());

		LOGGER.info("  Version: "
				+ ApplicationVersionHolder.INSTANCE.getApplicationVersion()
						.getVersion());
		LOGGER.info("  Build date: "
				+ ApplicationVersionHolder.INSTANCE.getApplicationVersion()
						.getBuildDate());

		LOGGER.info("  Loading configuration ...");

		final String configurationDirectory = servletContextEvent
				.getServletContext().getInitParameter("configurationDirectory");
		LOGGER.info("  configured configuration directory:  "
				+ configurationDirectory);
		ConfigurationDAO.setConfigurationDirectory(configurationDirectory);

		final String configurationFilename = servletContextEvent
				.getServletContext().getInitParameter("configurationFilename");

		if (StringUtils.isNotBlank(configurationFilename)) {
			LOGGER.info("  overriding default configuration filename config.xml with:  "
					+ configurationFilename);
			ConfigurationDAO.setConfigurationFilename(configurationFilename);
		}

		final Configuration config = ConfigurationDAO.getConfiguration();
		LOGGER.info("  Configuration loaded.");
		LOGGER.info("  Configured documentation content directory: "
				+ config.getTestDocumentationDirPath());
		LOGGER.info("  Updating documentation content directory (will be done asynchronously ...)");

		ScenarioDocuBuildsManager.INSTANCE
				.updateAllBuildsAndSubmitNewBuildsForImport();

		LOGGER.info("====================================================");
		LOGGER.info("Scenarioo webapplication server started succesfully.");
		LOGGER.info("====================================================");
	}

	private void initializeApplicationVersion(
			final ServletContext servletContext) {

		Properties properties = new Properties();
		InputStream inputStream = servletContext
				.getResourceAsStream("/WEB-INF/classes/version.properties");

		if (inputStream == null) {
			LOGGER.warn("  version.properties not found, no version information available");
			ApplicationVersionHolder.INSTANCE.initialize("unknown", "unknown");
			return;
		}

		try {
			properties.load(inputStream);
			ApplicationVersionHolder.INSTANCE
					.initializeFromProperties(properties);
		} catch (Exception e) {
			ApplicationVersionHolder.INSTANCE.initialize("unknown", "unknown");
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(final ServletContextEvent arg0) {
		LOGGER.info("===================================================");
		LOGGER.info("Scenarioo webapplication stopped.");
		LOGGER.info("===================================================");
	}
}
