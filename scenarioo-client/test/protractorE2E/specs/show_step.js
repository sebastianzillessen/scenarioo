'use strict';

var scenarioo = require('scenarioo-js');
var pages = require('./../webPages');

describeUseCaseE('Show step', {
    description: 'Show a single step of a scenario. Includes the screenshot, metadata nad navigation buttons'
}, function () {

    var homePage = new pages.homePage();
    var usecasePage = new pages.usecasePage();
    var scenarioPage = new pages.scenarioPage();
    var stepPage = new pages.stepPage();

    beforeEach(function(){
        new pages.homePage().initLocalStorage();
    });

    describeScenarioE('Navigation', {
        description: 'Navigate back and forth through the scenario steps.'
    }, function () {
        var ROUTE_OF_FIRST_STEP = '/step/Find%20Page/find_no_results/startSearch.jsp/0/0';
        var ROUTE_OF_SECOND_STEP = '/step/Find%20Page/find_no_results/startSearch.jsp/0/1';
        var ROUTE_OF_THIRD_STEP = '/step/Find%20Page/find_no_results/searchResults.jsp/0/0';

        homePage.goToPage();
        scenarioo.saveStep('Display home page with list of use cases');

        homePage.selectUseCase(1);
        scenarioo.saveStep('Display list of scenarios');

        usecasePage.selectScenario(1);
        scenarioo.saveStep('Display one scenario');

        scenarioPage.openStepByName('Step 1: Wikipedia Suche');
        stepPage.assertRoute(ROUTE_OF_FIRST_STEP);
        stepPage.assertPreviousStepIsDisabled();
        stepPage.assertPreviousPageIsDisabled();
        stepPage.assertNextStepIsEnabled();
        stepPage.assertNextPageIsEnabled();
        scenarioo.saveStep('First step of scenario. Back buttons are disabled.');

        stepPage.goToNextStep();
        stepPage.assertRoute(ROUTE_OF_SECOND_STEP);
        stepPage.assertPreviousStepIsEnabled();
        stepPage.assertPreviousPageIsDisabled();
        stepPage.assertNextStepIsEnabled();
        stepPage.assertNextPageIsEnabled();
        scenarioo.saveStep('Second step of scenario. Previous step button is now active.');

        stepPage.goToNextPage();
        stepPage.assertRoute(ROUTE_OF_THIRD_STEP);
        stepPage.assertPreviousStepIsEnabled();
        stepPage.assertPreviousPageIsEnabled();
        stepPage.assertNextStepIsDisabled();
        stepPage.assertNextPageIsDisabled();
        scenarioo.saveStep('Second step of scenario. Previous step button is now active.');

        stepPage.goToPreviousStep();
        stepPage.assertRoute(ROUTE_OF_SECOND_STEP);
        stepPage.goToPreviousStep();
        stepPage.assertRoute(ROUTE_OF_FIRST_STEP);
        stepPage.assertPreviousStepIsDisabled();
        stepPage.assertPreviousPageIsDisabled();
        stepPage.assertNextStepIsEnabled();
        stepPage.assertNextPageIsEnabled();
        scenarioo.saveStep('Back on the first step.');
    });

    describeScenarioE('Step does not exist', {
        description: 'If the requested step does not exist, an error message is shown.'
    }, function () {
        stepPage.goToPage('/step/Find Page/find_no_results/inexistent_page.jsp/0/42');
        stepPage.assertErrorMessageIsShown();

        scenarioo.saveStep('Error message.');
    });

    describeScenarioE('Fallback step exists', {
        description: 'A fallback message is shown in case the page does not exist but a fallback is found.'
    }, function () {
        stepPage.goToPage('/step/Find%20Page/renamed_scenario/searchResults.jsp/0/0');

        stepPage.assertFallbackMessageIsShown();
        stepPage.assertFallbackMessageContainsText('Scenario: find_multiple_results');

        scenarioo.saveStep('Fallback message.');
    });

    describeScenarioE('Fallback to best match', {
        description: 'If the fallback mechanism finds multiple candidates, the one with the most matching labels is used.'
    }, function() {
        stepPage.goToPage('/step/RenamedUseCase/DeletedScenario/contentPage.jsp/111/222?labels=exact%20match,i18n,step-label-2,public,page-label1,page-label2');

        stepPage.assertFallbackMessageIsShown();
        stepPage.assertFallbackMessageContainsText('Usecase: Switch Language');
        stepPage.assertFallbackMessageContainsText('Scenario: search_article_in_german_and_switch_to_spanish');
        stepPage.assertScenarioLabelsContain('i18n');
        scenarioo.saveStep('Of the 10 page variants, a fallback step with an i18n label is returned.');
    });

    describeScenarioE('Share step', {
        description: 'The step link popup shows the link to the step and to the image.'
    }, function () {
        stepPage.goToPage('/step/Find Page/find_no_results/startSearch.jsp/0/0');

        scenarioo.saveStep('A step.');

        stepPage.clickShareThisPageLink();
        stepPage.assertStepLinksDialogVisible();
        scenarioo.saveStep('Step links dialog.');
    });

    describeScenarioE('Metadata with link to object', {
        description: 'Click on a object link in Call tree and jump to object example.action.StartInitAction'
    }, function () {
        stepPage.goToPage('/step/Find%20Page/find_no_results/startSearch.jsp/0/0');

        stepPage.openMetadataTabIfClosed(1);
        scenarioo.saveStep('Expand Call tree panel');

        stepPage.clickOnLink('uiAction_example.action.StartInitAction');
        stepPage.assertToolTipInBreadcrumb('uiAction: example.action.StartInitAction');
    });

    describeScenarioE('HTML view of current step', 'If the step data contains html source data, it should be displayed in the HTML tab', function () {
        stepPage.goToPage('/step/Find%20Page/find_no_results/startSearch.jsp/0/0');
        scenarioo.saveStep('A step');

        stepPage.clickHtmlTabButton();
        scenarioo.saveStep('Switch to HTML tab');

        stepPage.assertHtmlSourceEquals('<html>\n<head>\n</head>\n<body>\n   <p>just some dummy html code</p>\n</body>\n</html>');

        stepPage.clickScreenshotTabButton();
        scenarioo.saveStep('Switch back to Screenshot tab');
    });

    describeScenarioE('Step without HTML source attached', 'If the step data contains no html source data, the HTML tab should not be displayed at all', function () {
        stepPage.goToPage('/step/Donate/find_donate_page/startSearch.jsp/0/0');
        stepPage.assertHtmlTabIsHidden();
        scenarioo.saveStep('A step with no HTML source attached');
    });

});
