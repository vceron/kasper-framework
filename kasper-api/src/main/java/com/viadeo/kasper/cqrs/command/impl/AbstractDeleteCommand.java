// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.UpdateCommand;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Convenient base implementation for id and version management
 * 
 */
public abstract class AbstractDeleteCommand implements UpdateCommand {
    private static final long serialVersionUID = -432287057423281452L;

    @NotNull
	private final KasperID id;

    private final Long version;

	// ------------------------------------------------------------------------

	public AbstractDeleteCommand(final KasperID id) {
		this.id = checkNotNull(id);
        this.version = null;
	}

 	public AbstractDeleteCommand(final KasperID id, final Long version) {
		this.id = checkNotNull(id);
        this.version = version; /* can be null */
	}

	// ------------------------------------------------------------------------

	@Override
	public KasperID getId() {
		return this.id;
	}

    @Override
    public Optional<Long> getVersion() {
        return Optional.fromNullable(this.version);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractDeleteCommand other = (AbstractDeleteCommand) obj;

        return com.google.common.base.Objects.equal(this.id, other.id)
                && com.google.common.base.Objects.equal(this.version, other.version);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(this.id, this.version);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .addValue(this.id)
                .addValue(this.version)
                .toString();
    }

}