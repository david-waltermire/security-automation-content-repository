/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 ssill2
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package org.scapdev.content.core.query;

import java.util.Map;

import org.scapdev.content.model.EntityInfo;

public interface EntityStatistic 
{
	/**
	 * Get the EntityInfo object this statistic is for.
	 * 
	 * @return EntityInfo
	 */
	EntityInfo getEntityInfo();
	
	/**
	 * Get the detailed relationship statistics for each entity instance of this
	 * EntityInfo object.
	 * 
	 * @return Map<String, RelationshipStatistic>
	 */
	Map<String, ? extends RelationshipStatistic> getRelationshipInfoStatistics();
	
	/**
	 * Get the number of Entity instances for this EntityInfo object.
	 * @return int
	 */
	int getCount();
}
