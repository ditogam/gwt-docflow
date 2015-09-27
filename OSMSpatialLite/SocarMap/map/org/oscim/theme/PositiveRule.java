/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.theme;

import org.oscim.core.Tag;

class PositiveRule extends Rule {
	final AttributeMatcher mKeyMatcher;
	final AttributeMatcher mValueMatcher;

	PositiveRule(int element, int closed, byte zoomMin, byte zoomMax,
			AttributeMatcher keyMatcher, AttributeMatcher valueMatcher) {
		super(element, closed, zoomMin, zoomMax);

		if (keyMatcher instanceof AnyMatcher)
			mKeyMatcher = null;
		else
			mKeyMatcher = keyMatcher;

		if (valueMatcher instanceof AnyMatcher)
			mValueMatcher = null;
		else
			mValueMatcher = valueMatcher;
	}

	@Override
	boolean matchesNode(Tag[] tags) {
		return (mKeyMatcher == null || mKeyMatcher.matches(tags))
				&& (mValueMatcher == null || mValueMatcher.matches(tags));
	}

	@Override
	boolean matchesWay(Tag[] tags) {
		return (mKeyMatcher == null || mKeyMatcher.matches(tags))
				&& (mValueMatcher == null || mValueMatcher.matches(tags));
	}
}
