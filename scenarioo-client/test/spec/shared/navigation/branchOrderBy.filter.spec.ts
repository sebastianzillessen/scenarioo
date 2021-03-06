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

var CFG_LAST_BUILD_DATE_DESCENDING = {'branchSelectionListOrder': 'last-build-date-descending'};
var CFG_NAME_ASCENDING = {'branchSelectionListOrder': 'name-ascending'};
var CFG_NAME_DESCENDING = {'branchSelectionListOrder': 'name-descending'};
var CFG_PROP_NOT_SET = {};

var DEFAULT_INPUT = [
    {
        isAlias: false,
        branch: {
            name: 'feature-102'
        }
    },
    {
        isAlias: true,
        branch: {
            name: 'release-1.3'
        }
    },
    {
        isAlias: true,
        branch: {
            name: 'release-1.4'
        }
    },
    {
        isAlias: false,
        branch: {
            name: 'feature-101'
        }
    }
];

describe('Filter scBranchOrderBy', function () {

    var ConfigService, $httpBackend, TestData;

    var scBranchOrderByFilter;

    beforeEach(async function () {
        angular.mock.module('scenarioo.controllers');
        angular.mock.module('scenarioo.services');
        angular.mock.module('scenarioo.filters');
    });

    function initConfig(config) {
        inject(function ($filter, _ConfigService_, _$httpBackend_, _TestData_) {

            ConfigService = _ConfigService_;
            $httpBackend = _$httpBackend_;
            TestData = _TestData_;

            $httpBackend.whenGET('rest/configuration').respond(config);

            ConfigService.load();
            $httpBackend.flush();

            scBranchOrderByFilter = $filter('scBranchOrderBy');
        });
    }


    describe('should handle invalid input gracefully:', function () {
        beforeEach(async function () {
            initConfig(CFG_LAST_BUILD_DATE_DESCENDING);
        });

        it('string', function () {
            var result = scBranchOrderByFilter('someString');
            expect(result).toEqual('someString');
        });
        it('object', function () {
            var result = scBranchOrderByFilter({some: 'object'});
            expect(result).toEqual({some: 'object'});
        });
        it('number', function () {
            var result = scBranchOrderByFilter(1);
            expect(result).toEqual(1);
        });
    });

    describe('should order given branch resource objects by last-build-date-descending:', function () {
        beforeEach(async function () {
            initConfig(CFG_LAST_BUILD_DATE_DESCENDING);
        });

        it('alias branches first', function () {
            var inputArray = [
                {
                    isAlias: false,
                    branch: {
                        name: 'A'
                    },
                    builds: [
                        {
                            build: {date: 0}
                        }
                    ]
                },
                {
                    isAlias: true,
                    branch: {
                        name: 'B'
                    },
                    builds: [
                        {
                            build: {date: 1}
                        }
                    ]
                }
            ];


            var result = scBranchOrderByFilter(inputArray);

            expect(result[0].branch.name).toEqual('B');
            expect(result[1].branch.name).toEqual('A');
        });

        it('then alphabetically (not case sensitive!)', function () {

            var inputArray = [
                {
                    isAlias: true,
                    branch: {
                        name: 'Ae'
                    }
                },
                {
                    isAlias: true,
                    branch: {
                        name: 'Ba'
                    }
                },
                {
                    isAlias: true,
                    branch: {
                        name: 'be'
                    }
                },
                {
                    isAlias: true,
                    branch: {
                        name: 'aa'
                    }
                }
            ];

            var result = scBranchOrderByFilter(inputArray);

            expect(result[0].branch.name).toEqual('aa');
            expect(result[1].branch.name).toEqual('Ae');
            expect(result[2].branch.name).toEqual('Ba');
            expect(result[3].branch.name).toEqual('be');
        });

        it('then by newest build date', function () {

            var inputArray = [
                {
                    isAlias: false,
                    branch: {
                        name: 'Ae'
                    },
                    builds: [
                        {
                            build: {date: 1}
                        }
                    ]
                },
                {
                    isAlias: false,
                    branch: {
                        name: 'Ba'
                    },
                    builds: [
                        {
                            build: {date: 2}
                        }
                    ]
                },
                {
                    isAlias: false,
                    branch: {
                        name: 'be'
                    },
                    builds: [
                        {
                            build: {date: 3}
                        }
                    ]
                },
                {
                    isAlias: false,
                    branch: {
                        name: 'aa'
                    },
                    builds: [
                        {
                            build: {date: 4}
                        }
                    ]
                }
            ];

            var result = scBranchOrderByFilter(inputArray);

            expect(result[0].branch.name).toEqual('aa');
            expect(result[1].branch.name).toEqual('be');
            expect(result[2].branch.name).toEqual('Ba');
            expect(result[3].branch.name).toEqual('Ae');
        });
    });

    describe('should order given branch resource objects by name-ascending:', function () {

        beforeEach(async function () {
            initConfig(CFG_NAME_ASCENDING);
        });

        it('then alphabetically (not case sensitive!)', function () {

            var result = scBranchOrderByFilter(DEFAULT_INPUT);

            expect(result[0].branch.name).toEqual('release-1.3');
            expect(result[1].branch.name).toEqual('release-1.4');
            expect(result[2].branch.name).toEqual('feature-101');
            expect(result[3].branch.name).toEqual('feature-102');
        });
    });

    describe('Should order given branch resource objects by name ascending as a default', function () {

        beforeEach(async function () {
            initConfig(CFG_PROP_NOT_SET);
        });

        it('then alphabetically (not case sensitive!)', function () {

            var result = scBranchOrderByFilter(DEFAULT_INPUT);

            expect(result[0].branch.name).toEqual('release-1.3');
            expect(result[1].branch.name).toEqual('release-1.4');
            expect(result[2].branch.name).toEqual('feature-101');
            expect(result[3].branch.name).toEqual('feature-102');
        });
    });

    describe('should order given branch resource objects by name-descending:', function () {

        beforeEach(async function () {
            initConfig(CFG_NAME_DESCENDING);
        });

        it('then alphabetically (not case sensitive!)', function () {

            var result = scBranchOrderByFilter(DEFAULT_INPUT);

            expect(result[0].branch.name).toEqual('release-1.4');
            expect(result[1].branch.name).toEqual('release-1.3');
            expect(result[2].branch.name).toEqual('feature-102');
            expect(result[3].branch.name).toEqual('feature-101');
        });
    });
});
