// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api.query;

import com.viadeo.kasper.api.annotation.XKasperQuery;
import com.viadeo.kasper.api.component.query.Query;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.domain.sample.hello.api.HelloDomain.*;

@XKasperQuery(description = "Retrieve all submitted Hello messages for a specific buddy")
public class GetAllHelloMessagesSentToBuddyQuery implements Query {

    @NotNull( message = NOT_PROVIDED_BUDDY_MSG )
    @Length( min = MIN_BUDDY_LENGTH, max = MAX_BUDDY_LENGTH, message = BAD_LENGTH_BUDDY_MSG )
    @Pattern( regexp = REGEX_BUDDY, message = INVALID_BUDDY_MSG)
    private final String forBuddy;

    // ------------------------------------------------------------------------

    public GetAllHelloMessagesSentToBuddyQuery(final String forBuddy) {
        this.forBuddy = checkNotNull(forBuddy);
    }

    public String getForBuddy() {
        return this.forBuddy;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (null == obj) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GetAllHelloMessagesSentToBuddyQuery other = (GetAllHelloMessagesSentToBuddyQuery) obj;
        return com.google.common.base.Objects.equal(this.forBuddy, other.forBuddy);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(this.forBuddy);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .addValue(this.forBuddy)
                .toString();
    }

}
