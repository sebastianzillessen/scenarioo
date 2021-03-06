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

import org.scenarioo.business.builds.ScenarioDocuBuildsManager;
import org.scenarioo.dao.aggregates.AggregatedDocuDataReader;
import org.scenarioo.dao.aggregates.ScenarioDocuAggregationDao;
import org.scenarioo.model.docu.aggregates.objects.LongObjectNamesResolver;
import org.scenarioo.repository.ConfigurationRepository;
import org.scenarioo.repository.RepositoryLocator;
import org.scenarioo.rest.base.BuildIdentifier;
import org.scenarioo.rest.base.ScenarioIdentifier;
import org.scenarioo.rest.base.StepIdentifier;
import org.scenarioo.rest.step.logic.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/branch/{branchName}/build/{buildName}/usecase/{usecaseName}/scenario")
public class ScreenshotResource {

	private final ConfigurationRepository configurationRepository = RepositoryLocator.INSTANCE
			.getConfigurationRepository();

	private final LongObjectNamesResolver longObjectNamesResolver = new LongObjectNamesResolver();
	private final AggregatedDocuDataReader aggregatedDataReader = new ScenarioDocuAggregationDao(
			configurationRepository.getDocumentationDataDirectory(), longObjectNamesResolver);
	private final ScenarioLoader scenarioLoader = new ScenarioLoader(aggregatedDataReader);
	private final StepIndexResolver stepIndexResolver = new StepIndexResolver();
	private final StepLoader stepImageInfoLoader = new StepLoader(scenarioLoader, stepIndexResolver);
	private final ScreenshotResponseFactory screenshotResponseFactory = new ScreenshotResponseFactory();
	private final LabelsQueryParamParser labelsQueryParamParser = new LabelsQueryParamParser();

	/**
	 * This method is used internally for loading the image of a step. It is the faster method, because it already knows
	 * the filename of the image.
	 */
	@GetMapping(path = "{scenarioName}/image/{imageFileName}", produces = "image/jpeg")
	public ResponseEntity getScreenshot(@PathVariable("branchName") final String branchName,
			@PathVariable("buildName") final String buildName, @PathVariable("usecaseName") final String usecaseName,
			@PathVariable("scenarioName") final String scenarioName, @PathVariable("imageFileName") final String imageFileName) {

		BuildIdentifier buildIdentifier = ScenarioDocuBuildsManager.INSTANCE.resolveBranchAndBuildAliases(branchName,
				buildName);
		ScenarioIdentifier scenarioIdentifier = new ScenarioIdentifier(buildIdentifier, usecaseName, scenarioName);

		return screenshotResponseFactory.createFoundImageResponse(scenarioIdentifier, imageFileName, false);
	}

	/**
	 * This method is used for sharing screenshot images. It is a bit slower, because the image filename has to be
	 * resolved first. But it is also more stable, because it uses the new "stable" URL pattern.
	 */
	@GetMapping(path = "{scenarioName}/pageName/{pageName}/pageOccurrence/{pageOccurrence}/stepInPageOccurrence/{stepInPageOccurrence}/image.{extension}", produces = "image/jpeg")
	public ResponseEntity getScreenshotStable(@PathVariable("branchName") final String branchName,
											  @PathVariable("buildName") final String buildName, @PathVariable("usecaseName") final String usecaseName,
											  @PathVariable("scenarioName") final String scenarioName, @PathVariable("pageName") final String pageName,
											  @PathVariable("pageOccurrence") final int pageOccurrence,
											  @PathVariable("stepInPageOccurrence") final int stepInPageOccurrence,
											  @RequestParam(value="fallback", required = false) final boolean fallback, @RequestParam(value="labels", required = false) final String labels) {

		BuildIdentifier buildIdentifierBeforeAliasResolution = new BuildIdentifier(branchName, buildName);
		BuildIdentifier buildIdentifier = ScenarioDocuBuildsManager.INSTANCE.resolveBranchAndBuildAliases(branchName,
				buildName);
		StepIdentifier stepIdentifier = new StepIdentifier(buildIdentifier, usecaseName, scenarioName, pageName,
				pageOccurrence, stepInPageOccurrence, labelsQueryParamParser.parseLabels(labels));

		StepLoaderResult stepImageInfo = stepImageInfoLoader.loadStep(stepIdentifier);

		return screenshotResponseFactory.createResponse(stepImageInfo, fallback, buildIdentifierBeforeAliasResolution);
	}

}
