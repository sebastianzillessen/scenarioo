/* scenarioo-server
 * Copyright (C) 2015, scenarioo.org Development Team
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

package org.scenarioo.rest.issue;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.scenarioo.dao.design.aggregates.IssueAggregationDAO;
import org.scenarioo.dao.design.entities.DesignFiles;
import org.scenarioo.model.design.aggregates.IssueScenarioSketches;
import org.scenarioo.model.design.aggregates.IssueSummary;
import org.scenarioo.model.design.aggregates.ScenarioSketchSummary;
import org.scenarioo.model.design.entities.Issue;
import org.scenarioo.model.design.entities.ScenarioSketch;
import org.scenarioo.repository.ConfigurationRepository;
import org.scenarioo.repository.RepositoryLocator;
import org.scenarioo.rest.base.BuildIdentifier;
import org.scenarioo.utils.design.readers.DesignReader;

@Path("/rest/branch/{branchName}/issue")
public class IssuesResource {

	private static final Logger LOGGER = Logger.getLogger(IssuesResource.class);

	private final ConfigurationRepository configurationRepository = RepositoryLocator.INSTANCE
			.getConfigurationRepository();

	private final IssueAggregationDAO dao = new IssueAggregationDAO(configurationRepository.getDesignDataDirectory());

	// TODO: Extract the functionality these provide into separate classes
	private final DesignReader reader = new DesignReader(configurationRepository.getDesignDataDirectory());
	private final DesignFiles files = new DesignFiles(configurationRepository.getDesignDataDirectory());

	/**
	 * Lightweight call, which does not send all scenario sketch information.
	 */
	@GET
	@Produces({ "application/xml", "application/json" })
	public List<IssueSummary> loadIssueSummaries(@PathParam("branchName") final String branchName) {
		LOGGER.info("REQUEST: loadIssueSummaryList(" + branchName + ")");
		BuildIdentifier ident = new BuildIdentifier(branchName, "");
		List<IssueSummary> result = new LinkedList<IssueSummary>();

		// This does not use pre-aggregated data
		// Temporary Solution, probably does not scale
		List<Issue> issues = reader.loadIssues(branchName);
		for (Issue i : issues) {
			List<ScenarioSketch> scenarioSketches = reader.loadScenarioSketches(branchName, i.getIssueId());
			IssueSummary summary = new IssueSummary();
			summary.setName(i.getName());
			summary.setId(i.getIssueId());
			summary.setDescription(i.getDescription());
			summary.setStatus(i.getIssueStatus());
			summary.setNumberOfScenarioSketches(scenarioSketches.size());
			summary.setLabels(i.getLabels());
			result.add(summary);
		}
		return result;
	}

	@GET
	@Produces({ "application/xml", "application/json" })
	@Path("/{issueId}")
	public IssueScenarioSketches loadIssueScenarioSketches(@PathParam("branchName") final String branchName,
			@PathParam("issueId") final String issueId) {
		LOGGER.info("REQUEST: loadIssueScenarioSketches(" + issueId + ")");
		// return dao.loadIssueProposals(new BuildIdentifier(branchName, ""), issueId);

		// This does not use pre-aggregated data
		// Temporary Solution, probably does not scale
		Issue issue = reader.loadIssue(branchName, issueId);
		List<ScenarioSketch> scenarioSketches = reader.loadScenarioSketches(branchName, issueId);
		List<ScenarioSketchSummary> summaries = new ArrayList<ScenarioSketchSummary>();
		for (ScenarioSketch sketch : scenarioSketches) {
			ScenarioSketchSummary summary = new ScenarioSketchSummary();
			summary.setScenarioSketch(sketch);
			summary.setNumberOfSteps(reader.loadSketchSteps(branchName, issueId, sketch.getScenarioSketchName()).size());
			summaries.add(summary);
		}
		IssueScenarioSketches result = new IssueScenarioSketches();
		result.setIssue(issue);
		result.setScenarioSketches(summaries);
		return result;

	}

	@POST
	@Consumes("application/json")
	public Response storeNewIssue(@PathParam("branchName") final String branchName,
			final Issue newIssue) {
		// TODO: Make sure we do not overwrite an existing issue without confirmation! If we do, do not overwrite the
		// hash. Maybe have a different URL, using the id for accessing/updating existing issues
		LOGGER.info("Now storing a new issue.");
		LOGGER.info(newIssue);
		LOGGER.info("-----------------------");
		MessageDigest converter;
		try {
			converter = MessageDigest.getInstance("SHA1");
			String id = new String(Hex.encodeHex(converter.digest(newIssue.toString().getBytes())));
			id = id.substring(0, 7); // limit to first 7 characters, like in git, to keep URLs short
			newIssue.setIssueId(id.toString());
		} catch (NoSuchAlgorithmException e) {
			LOGGER.info("Couldn't generate SHA1 message digest.");
			return Response.serverError().build();
		}
		files.writeIssueToFile(branchName, newIssue);
		return Response.ok(newIssue, MediaType.APPLICATION_JSON).build();
	}

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	@Path("/{issueId}")
	public Response updateIssue(@PathParam("branchName") final String branchName,
			@PathParam("issueId") final String issueId,
			final Issue updatedIssue) {
		LOGGER.info("Now updating an existing issue.");
		LOGGER.info(updatedIssue.getIssueId());
		LOGGER.info("-----------------------");
		if (updatedIssue.getIssueId() == null) {
			LOGGER.error("There was no IssueID set on the issue object!");
			updatedIssue.setIssueId(issueId);
		}
		Issue existingIssue = reader.loadIssue(branchName, issueId);
		existingIssue.update(updatedIssue);
		files.updateIssue(branchName, existingIssue);

		return Response.ok(existingIssue, MediaType.APPLICATION_JSON).build();
	}

	private IssueSummary mapSummary(final IssueScenarioSketches issueProposals) {
		IssueSummary summary = new IssueSummary();
		Issue issue = issueProposals.getIssue();
		summary.setName(issue.getName());
		summary.setDescription(issue.getDescription());
		summary.setStatus(issue.getIssueStatus());
		summary.setNumberOfScenarioSketches(issueProposals.getScenarioSketches().size());
		summary.setLabels(issue.getLabels());
		return summary;
	}
}