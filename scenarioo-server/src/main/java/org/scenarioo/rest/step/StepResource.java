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

package org.scenarioo.rest.step;

import org.apache.log4j.Logger;
import org.scenarioo.api.ScenarioDocuReader;
import org.scenarioo.business.builds.ScenarioDocuBuildsManager;
import org.scenarioo.dao.aggregates.AggregatedDocuDataReader;
import org.scenarioo.dao.aggregates.ScenarioDocuAggregationDao;
import org.scenarioo.model.docu.aggregates.objects.LongObjectNamesResolver;
import org.scenarioo.repository.ConfigurationRepository;
import org.scenarioo.repository.RepositoryLocator;
import org.scenarioo.rest.base.BuildIdentifier;
import org.scenarioo.rest.base.StepIdentifier;
import org.scenarioo.rest.step.logic.LabelsQueryParamParser;
import org.scenarioo.rest.step.logic.ScenarioLoader;
import org.scenarioo.rest.step.logic.StepIndexResolver;
import org.scenarioo.rest.step.logic.StepLoader;
import org.scenarioo.rest.step.logic.StepLoaderResult;
import org.scenarioo.rest.step.logic.StepResponseFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/branch/{branchName}/build/{buildName}/usecase/{usecaseName}/scenario/{scenarioName}/pageName/{pageName}/pageOccurrence/{pageOccurrence}/stepInPageOccurrence/{stepInPageOccurrence}")
public class StepResource {

	private static final Logger LOGGER = Logger.getLogger(StepResource.class);

	private final ConfigurationRepository configurationRepository = RepositoryLocator.INSTANCE
			.getConfigurationRepository();

	private final LongObjectNamesResolver longObjectNamesResolver = new LongObjectNamesResolver();
	private final AggregatedDocuDataReader aggregatedDataReader = new ScenarioDocuAggregationDao(
			configurationRepository.getDocumentationDataDirectory(), longObjectNamesResolver);

	private final LabelsQueryParamParser labelsQueryParamParser = new LabelsQueryParamParser();
	private final ScenarioLoader scenarioLoader = new ScenarioLoader(aggregatedDataReader);
	private final StepIndexResolver stepIndexResolver = new StepIndexResolver();
	private final StepLoader stepLoader = new StepLoader(scenarioLoader, stepIndexResolver);

	private final ScenarioDocuReader scenarioDocuReader = new ScenarioDocuReader(
			configurationRepository.getDocumentationDataDirectory());

	private final StepResponseFactory stepResponseFactory = new StepResponseFactory(aggregatedDataReader,
			scenarioDocuReader);

	/**
	 * Get a step with all its data (meta data, html, ...) together with additional calculated navigation data
	 */
	@GetMapping
	public ResponseEntity loadStep(@PathVariable("branchName") final String branchName,
								   @PathVariable("buildName") final String buildName, @PathVariable("usecaseName") final String usecaseName,
								   @PathVariable("scenarioName") final String scenarioName, @PathVariable("pageName") final String pageName,
								   @PathVariable("pageOccurrence") final int pageOccurrence,
								   @PathVariable("stepInPageOccurrence") final int stepInPageOccurrence,
								   @RequestParam(value = "fallback", required = false) final boolean addFallbackInfo, @RequestParam(value = "labels", required = false) final String labels) {

		BuildIdentifier buildIdentifierBeforeAliasResolution = new BuildIdentifier(branchName, buildName);
		BuildIdentifier buildIdentifier = ScenarioDocuBuildsManager.INSTANCE.resolveBranchAndBuildAliases(branchName,
				buildName);
		StepIdentifier stepIdentifier = new StepIdentifier(buildIdentifier, usecaseName, scenarioName, pageName,
				pageOccurrence, stepInPageOccurrence, labelsQueryParamParser.parseLabels(labels));

		LOGGER.info("loadStep(" + stepIdentifier + ")");

		StepLoaderResult stepLoaderResult = stepLoader.loadStep(stepIdentifier);

		return stepResponseFactory.createResponse(stepLoaderResult, stepIdentifier,
				buildIdentifierBeforeAliasResolution, addFallbackInfo);
	}

}
