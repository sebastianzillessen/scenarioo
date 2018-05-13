package org.scenarioo.rest.application;

import org.scenarioo.rest.base.AbstractBuildContentResource;
import org.scenarioo.rest.base.logging.ApplyRequestLogging;
import org.scenarioo.rest.builds.BranchBuildsResource;
import org.scenarioo.rest.builds.BuildsResource;
import org.scenarioo.rest.configuration.BranchAliasesResource;
import org.scenarioo.rest.configuration.ConfigurationResource;
import org.scenarioo.rest.configuration.LabelConfigurationsResource;
import org.scenarioo.rest.diffViewer.*;
import org.scenarioo.rest.objectRepository.CustomTabsResource;
import org.scenarioo.rest.objectRepository.GenericObjectsResource;
import org.scenarioo.rest.objectRepository.ObjectStepResource;
import org.scenarioo.rest.scenario.ScenariosResource;
import org.scenarioo.rest.search.SearchResource;
import org.scenarioo.rest.sketcher.issue.IssueResource;
import org.scenarioo.rest.sketcher.scenarioSketch.ScenarioSketchResource;
import org.scenarioo.rest.sketcher.stepSketch.SketchImageResource;
import org.scenarioo.rest.sketcher.stepSketch.StepSketchResource;
import org.scenarioo.rest.step.ScreenshotResource;
import org.scenarioo.rest.step.StepResource;
import org.scenarioo.rest.usecase.UseCasesResource;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplyRequestLogging
public class ScenariooRestApplication extends Application {

	private Set<Object> singletons = new HashSet<Object>();

	public ScenariooRestApplication() {
		// find . -name \*Resource.java
		singletons.add(new BranchAliasesResource());
		singletons.add(new ConfigurationResource());
		singletons.add(new LabelConfigurationsResource());
		singletons.add(new StepSketchResource());
		singletons.add(new SketchImageResource());
		singletons.add(new ScenarioSketchResource());
		singletons.add(new IssueResource());
		singletons.add(new ScreenshotResource());
		singletons.add(new StepResource());
		singletons.add(new BranchBuildsResource());
		singletons.add(new BuildsResource());
		singletons.add(new ComparisonsResource());
		singletons.add(new SearchResource());
		singletons.add(new ScenariosResource());
		singletons.add(new GenericObjectsResource());
		singletons.add(new CustomTabsResource());
		singletons.add(new ObjectStepResource());
		singletons.add(new UseCaseDiffInfoResource());
		singletons.add(new BuildDiffInfoResource());
		singletons.add(new StepDiffScreenshotResource());
		singletons.add(new ScenarioDiffInfoResource());
		singletons.add(new StepDiffInfoResource());
		singletons.add(new VersionResource());
		singletons.add(new UseCasesResource());
		singletons.add(new AbstractBuildContentResource());
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

}
