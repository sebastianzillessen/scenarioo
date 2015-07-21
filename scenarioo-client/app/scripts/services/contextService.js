/* scenarioo-client
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

angular.module('scenarioo.services').service('ContextService', function (HostnameAndPort, SelectedBranchAndBuild) {

    var issueName, issueId, scenarioSketchName, scenarioSketchId, sketchStepIndex;
    var usecaseLink, scenarioLink, stepLink;
    var usecaseName, scenarioName, stepName;

    function initializeContext(){
        issueId = null;
        issueName = null;
        scenarioSketchName = null;
        scenarioSketchId = null;
        sketchStepIndex = null;

        usecaseName = null;
        scenarioName = null;
        stepName = null;

        usecaseLink = null;
        scenarioLink = null;
        stepLink = null;
    }

    function setUseCase(originalUsecaseName){
        this.usecaseName = prettifyName(originalUsecaseName);
        this.usecaseLink = createLink(originalUsecaseName);
    }

    function setScenario(originalUsecaseName, originalScenarioName){
        this.scenarioName = prettifyName(originalScenarioName);
        this.scenarioLink = createLink(originalUsecaseName, originalScenarioName);
    }

    return {
        issueId: issueId,
        issueName: issueName,
        scenarioSketchId: scenarioSketchId,
        scenarioSketchName: scenarioSketchName,
        sketchStepIndex: sketchStepIndex,
        usecaseName: usecaseName,
        scenarioName: scenarioName,
        stepName: stepName,
        usecaseLink: usecaseLink,
        scenarioLink: scenarioLink,
        stepLink: stepLink,

        initialize: initializeContext,
        setUseCase: setUseCase,
        setScenario: setScenario
    };

    function prettifyName(name){
        var wordsInName = name.split('_');
        wordsInName[0] = wordsInName[0].charAt(0).toUpperCase() + wordsInName[0].slice(1);
        return wordsInName.join(' ');
    }

    //Name has been called ID here to avoid naming conflicts in this file
    function createLink(usecaseId, scenarioId) {
        if (!scenarioId){
            return HostnameAndPort.forLinkAbsolute() + 'rest/branch/' + SelectedBranchAndBuild.selected()[SelectedBranchAndBuild.BRANCH_KEY] +
                '/build/' + SelectedBranchAndBuild.selected()[SelectedBranchAndBuild.BUILD_KEY] +
                '/usecase/' + usecaseId;
        }
        else {
            return HostnameAndPort.forLinkAbsolute() + 'rest/branch/' + SelectedBranchAndBuild.selected()[SelectedBranchAndBuild.BRANCH_KEY] +
                '/build/' + SelectedBranchAndBuild.selected()[SelectedBranchAndBuild.BUILD_KEY] +
                '/usecase/' + usecaseId +
                '/scenario/' + scenarioId;
        }
    }

});