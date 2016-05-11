/* scenarioo-client
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

'use strict';

describe('UseCasesTabController', function () {

    var $location;
    var useCasesTabController;

    beforeEach(module('scenarioo.controllers'));

    beforeEach(inject(function ($controller, $rootScope, _$location_) {
            $location = _$location_;

        useCasesTabController = $controller('UseCasesTabController');
        }
    ));

    it('has no usecases and builds set in the beginning', function () {
        expect(useCasesTabController.useCases).toBeUndefined();
        expect(useCasesTabController.branchesAndBuilds).toBeUndefined();
    });

    it('navigates to use case when link is clicked', function () {
        expect($location.path()).toBe('');

        useCasesTabController.goToUseCase('DisplayWeather');

        expect($location.path()).toBe('/usecase/DisplayWeather');
    });

});
