// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.values.impl;

import java.io.Serializable;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.ddd.values.IValue;

/**
 *
 * A base value used to enclose a classical type, ex : PersonName extends KasperEnclosingValue<String
 *
 * @param <PAYLOAD> The enclosed type
 */
public abstract class AbstractEnclosingValue<PAYLOAD extends Serializable> 
		implements IValue {

	private static final long serialVersionUID = -2912518894544850152L;

	protected final PAYLOAD value;
	
	// ------------------------------------------------------------------------
	
	public AbstractEnclosingValue(final PAYLOAD value) {
		super();
		
		this.value = Preconditions.checkNotNull(value);
	}
	
	public PAYLOAD get() {
		return value;
	}

	// ------------------------------------------------------------------------
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object _otherValue) {
		if (this == Preconditions.checkNotNull(_otherValue)) {
			return true;
		}
		if (this.getClass().isInstance(_otherValue)) {
			@SuppressWarnings("unchecked")
			final AbstractEnclosingValue<PAYLOAD> other = (AbstractEnclosingValue<PAYLOAD>) _otherValue;
			return value.equals(other.value);
		}
		return false;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
}