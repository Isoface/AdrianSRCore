/*
 * Copyright (c) 1997, 2007, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.hotmail.AdrianSR.core.iterator;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * An iterator for lists that allows the programmer
 * to traverse the list in either direction, modify
 * the list during iteration, and obtain the iterator's
 * current position in the list. A {@code ListIterator}
 * has no current element; its <I>cursor position</I> always
 * lies between the element that would be returned by a call
 * to {@code previous()} and the element that would be
 * returned by a call to {@code next()}.
 * An iterator for a list of length {@code n} has {@code n+1} possible
 * cursor positions, as illustrated by the carets ({@code ^}) below:
 * <PRE>
 *                      Element(0)   Element(1)   Element(2)   ... Element(n-1)
 * cursor positions:  ^            ^            ^            ^                  ^
 * </PRE>
 * Note that the {@link #remove} and {@link #set(Object)} methods are
 * <i>not</i> defined in terms of the cursor position;  they are defined to
 * operate on the last element returned by a call to {@link #next} or
 * {@link #previous()}.
 * <p>
 * This interface is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 * <p>
 * @author AdrianSR
 * @see Collection
 * @see List
 * @see Iterator
 * @see Enumeration
 * @see List#listIterator()
 * @param <E> data type.
 */
public class GenericIterator<E> implements ListIterator<E> {
	
	/**
	 * Class values.
	 */
	private final List<E>          data; // data list.
	private final String   generic_type; // generic data type name.
    private       int         nextIndex; // next to index.
    private       Boolean lastDirection;
	
    /**
     * Construct a new Generic
     * {@link ListIterator}.
     * <p>
     * @see List#listIterator()
     * @param data The data list.
     */
	public GenericIterator(List<E> data) {
		this.data         = data;
		this.nextIndex    = 0;
		this.generic_type = data.size() > 0 ? data.get(0).getClass().getSimpleName() : "Object";
	}
	
	@Override
	public boolean hasNext() {
		return nextIndex < data.size();
	}

	@Override
	public E next() {
		// get last direction.
		lastDirection = Boolean.TRUE;
		
		// return next in data list.
		return data.get(nextIndex ++);
	}
	
	@Override
	public int nextIndex() {
		return nextIndex;
	}

	@Override
	public boolean hasPrevious() {
		return nextIndex > 0;
	}

	@Override
	public E previous() {
		// set not last direction.
		lastDirection = Boolean.FALSE;
		
		// return previous in data list.
		return data.get(-- nextIndex);
	}

	@Override
	public int previousIndex() {
		return nextIndex - 1;
	}
	
	@Override
	public void set(E value) {
		// check last direction.
        if (lastDirection == null) {
            throw new IllegalStateException("No current " + generic_type);
        }
        
        // set value
        data.set(lastDirection ? nextIndex - 1 : nextIndex, value);
	}
	
	@Override
	public void add(E value) {
		throw new UnsupportedOperationException("Cannot modify the data of an iterator list!");
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Cannot modify the data of an iterator list!");
	}
}