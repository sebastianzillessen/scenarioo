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

/* global SVG:false */
/* eslint no-extra-semi:0*/

;(function () {
    SVG.extend(SVG.Nested, SVG.Shape, {

        isSelected: false,
        selectOptions: {rotationPoint: false, deepSelect: true},

        selectToggle: function () {
            this.isSelected = !this.isSelected;

            if(this instanceof SVG.ArrowShape) {
                this.line.select(this.selectOptions);
            } else {
                this.select(this.selectOptions);
            }

            this.draggable(this.isSelected);

            if (this.isSelected) {
                this.resize();
                this.fire('selected');
            } else {
                this.resize('stop');
                this.fire('unselected');
            }

            return this;
        },

        unSelect: function () {
            this.isSelected = false;

            if(this instanceof SVG.ArrowShape) {
                this.line.select(false, this.selectOptions);
            } else {
                this.select(false, this.selectOptions);
            }

            this.draggable(false);
            this.resize('stop');
            this.fire('unselected');
        }

    });
}).call(this);
