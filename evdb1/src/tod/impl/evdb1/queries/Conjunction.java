/*
TOD - Trace Oriented Debugger.
Copyright (c) 2006-2008, Guillaume Pothier
All rights reserved.

This program is free software; you can redistribute it and/or 
modify it under the terms of the GNU General Public License 
version 2 as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful, 
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
General Public License for more details.

You should have received a copy of the GNU General Public License 
along with this program; if not, write to the Free Software 
Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
MA 02111-1307 USA

Parts of this work rely on the MD5 algorithm "derived from the 
RSA Data Security, Inc. MD5 Message-Digest Algorithm".
*/
package tod.impl.evdb1.queries;

import tod.impl.database.IBidiIterator;
import tod.impl.dbgrid.merge.ConjunctionIterator;
import tod.impl.dbgrid.messages.GridEvent;
import tod.impl.evdb1.db.EventList;
import tod.impl.evdb1.db.IndexMerger;
import tod.impl.evdb1.db.IndexSet;
import tod.impl.evdb1.db.Indexes;
import tod.impl.evdb1.db.StdIndexSet.StdTuple;

/**
 * A conjunctive condition: all subconditions must match.
 * @author gpothier
 */
public class Conjunction extends CompoundCondition
{
	private static final long serialVersionUID = 6155046517220795498L;
	
	private final boolean itsMatchRoles;
	private final boolean itsFitlerDuplicates;
	
	/**
	 * @param aMatchRoles See {@link ConjunctionIterator}
	 */
	public Conjunction(boolean aMatchRoles, boolean aFilterDuplicates)
	{
		itsMatchRoles = aMatchRoles;
		itsFitlerDuplicates = aFilterDuplicates;
	}

	@Override
	public IBidiIterator<StdTuple> createTupleIterator(
			EventList aEventList,
			Indexes aIndexes, long aTimestamp)
	{
		IBidiIterator<StdTuple>[] theIterators = new IBidiIterator[getConditions().size()];
		int i = 0;
		for (EventCondition theCondition : getConditions())
		{
			theIterators[i++] = theCondition.createTupleIterator(aEventList, aIndexes, aTimestamp);
		}
		
		IBidiIterator<StdTuple> theIterator = IndexMerger.conjunction(itsMatchRoles, theIterators);
		return itsFitlerDuplicates ? IndexSet.createFilteredIterator(theIterator) : theIterator;
	}

	public boolean _match(GridEvent aEvent)
	{
		for (EventCondition theCondition : getConditions())
		{
			if (! theCondition._match(aEvent)) return false;
		}
		return true;
	}
	
}
