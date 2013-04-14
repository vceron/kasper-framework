// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.ddd.values.impl;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.ddd.values.IReferenceValuesRepository;

/**
 *
 * A convenient class that can be used to store enums a reference values repository
 *
 * @param <ENUM>
 */
public abstract class AbstractEnumRepository<ENUM extends Enum<?>> 
		implements IReferenceValuesRepository<AbstractReferenceValue<ENUM>, ENUM> {

	/**
	 * The enclosing value type managed by this repository
	 */
	public class Value extends AbstractReferenceValue<ENUM> {

		private static final long serialVersionUID = 1794937696257943048L;

		Value(final ENUM _value) {
			super(_value);
		}

		@Override
		public Long getId() {
			return Long.valueOf(this.value.ordinal());
		}

		@Override
		public boolean equals(final Object _otherValue) {
			if (null == _otherValue) {
				return false;
			}
			if (this == _otherValue) {
				return true;
			}
			if (this.getClass().isInstance(_otherValue)) {
				@SuppressWarnings("unchecked")
				final Value other = (Value) _otherValue;
				return this.value.equals(other.value);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.value.hashCode();
		}

	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.ddd.values.IReferenceValuesRepository#build(java.io.Serializable)
	 */
	@Override
	public Value build(final ENUM value) {
		return new Value(Preconditions.checkNotNull(value));
	}
	
	/**
	 * @see com.viadeo.kasper.ddd.values.IReferenceValuesRepository#getDefault()
	 */
	@Override
	public Value getDefault() {
		return new Value(this.getDefaultEnumValue());
	}

	// ------------------------------------------------------------------------

	/**
	 * @return the default value for this repository
	 */
	abstract protected ENUM getDefaultEnumValue();

}