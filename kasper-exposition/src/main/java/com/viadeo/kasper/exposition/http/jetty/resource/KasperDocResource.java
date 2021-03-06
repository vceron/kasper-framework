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
package com.viadeo.kasper.exposition.http.jetty.resource;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.viadeo.kasper.doc.element.*;
import com.viadeo.kasper.doc.nodes.RetMap;
import com.viadeo.kasper.doc.nodes.RetUnexistent;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Path("/kasper/doc")
public class KasperDocResource implements Resource {

    private static final String DEFAULT_UNSPECIFIED = "unspecified";

    public static final Function<DocumentedDomain,LightDocumentedDomain> LIGHTER = new Function<DocumentedDomain, LightDocumentedDomain>() {
        @Override
        public LightDocumentedDomain apply(final DocumentedDomain documentedDomain) {
            return new LightDocumentedDomain(checkNotNull(documentedDomain));
        }
    };

    private final DocumentedPlatform documentedPlatform;

    // ------------------------------------------------------------------------

    public KasperDocResource(final DocumentedPlatform documentedPlatform) {
        this.documentedPlatform = documentedPlatform;
    }

    // ------------------------------------------------------------------------

    @GET
    @Path("domains")
    @Produces(MediaType.APPLICATION_JSON)
    public RetMap getDomains() {
        return new RetMap(DocumentedElementType.DOMAIN.getType(), Collections2.transform(documentedPlatform.getDomains(), LIGHTER));
    }

    /*
     * Return Object so json provider implementation will look after the runtime type of the
     * the object, otherwise it would only serialize using fields from returned type.
     */
    @GET
    @Path("domain/{domainName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getDomain(@PathParam("domainName") final String domainName) {
        if (null == domainName) {
            return new RetUnexistent(DocumentedElementType.DOMAIN.getType(), DEFAULT_UNSPECIFIED);
        }

        final Optional<DocumentedDomain> domain = documentedPlatform.getDomain(domainName);

        if ( ! domain.isPresent()) {
            return new RetUnexistent(DocumentedElementType.DOMAIN.getType(), domainName);
        }

        return new LightDocumentedDomain(domain.get());
    }

    // ------------------------------------------------------------------------

    @GET
    @Path("domain/{domainName}/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getEntities(@PathParam("domainName") final String domainName,
                              @PathParam("type") final String type) {
        if (null == domainName) {
            return new RetUnexistent(DocumentedElementType.DOMAIN.getType(), DEFAULT_UNSPECIFIED);
        }

        final Optional<DocumentedDomain> domain = documentedPlatform.getDomain(domainName);

        if ( ! domain.isPresent()) {
            return new RetUnexistent(DocumentedElementType.DOMAIN.getType(), domainName);
        }

        final Optional<DocumentedElementType> documentedElementType = DocumentedElementType.of(type);

        if (documentedElementType.isPresent() && documentedElementType.get().getPluralType().equals(type)) {
            final DocumentedElementType elementType = documentedElementType.get();
            final List<AbstractDomainElement> documentedElements = get(domain.get(), elementType);
            return new RetMap(elementType.getType(), documentedElements);
        }

        return new RetUnexistent("type", type);
    }

    // ------------------------------------------------------------------------

    @GET
    @Path("domain/{domainName}/{type}/{entityName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getEntity(@PathParam("domainName") final String domainName,
                            @PathParam("type") final String type,
                            @PathParam("entityName") final String entityName) {

        if (null == domainName) {
            return new RetUnexistent(DocumentedElementType.DOMAIN.getType(), DEFAULT_UNSPECIFIED);
        }

        final Optional<DocumentedDomain> domain = documentedPlatform.getDomain(domainName);

        if ( ! domain.isPresent()) {
            return new RetUnexistent(DocumentedElementType.DOMAIN.getType(), DEFAULT_UNSPECIFIED);
        }

        final Optional<DocumentedElementType> documentedElementType = DocumentedElementType.of(type);

        if (documentedElementType.isPresent()) {
            final DocumentedElementType elementType = documentedElementType.get();
            final Optional<AbstractDomainElement> documentedElement = get(domain.get(), elementType, entityName);

            if (documentedElement.isPresent()) {
                return documentedElement.get();
            }

            return new RetUnexistent(elementType.getType(), entityName);
        }

        return new RetUnexistent("type", type);
    }

    // ------------------------------------------------------------------------

    private static Optional<AbstractDomainElement> get(final DocumentedDomain documentedDomain,
                                                       final DocumentedElementType type,
                                                       final String name) {
        for (final AbstractDomainElement documentedDomainElement : get(documentedDomain, type)) {
            if (documentedDomainElement.getName().equals(name)) {
                return Optional.of(documentedDomainElement);
            }
        }
        return Optional.absent();
    }

    private static List<AbstractDomainElement> get(final DocumentedDomain documentedDomain,
                                                   final DocumentedElementType type) {
        switch (type) {
            case COMMAND:
                return Lists.<AbstractDomainElement>newArrayList(documentedDomain.getCommands());
            case COMMAND_HANDLER:
                return Lists.<AbstractDomainElement>newArrayList(documentedDomain.getCommandHandlers());
            case QUERY:
                return Lists.<AbstractDomainElement>newArrayList(documentedDomain.getQueries());
            case QUERY_RESULT:
                return Lists.<AbstractDomainElement>newArrayList(documentedDomain.getQueryResults());
            case QUERY_HANDLER:
                return Lists.<AbstractDomainElement>newArrayList(documentedDomain.getQueryHandlers());
            case EVENT:
                return Lists.<AbstractDomainElement>newArrayList(documentedDomain.getEvents());
            case DECLARED_EVENT:
                return Lists.<AbstractDomainElement>newArrayList(documentedDomain.getDeclaredEvents());
            case REFERENCED_EVENT:
                return Lists.<AbstractDomainElement>newArrayList(documentedDomain.getReferencedEvents());
            case EVENT_LISTENER:
                return Lists.<AbstractDomainElement>newArrayList(documentedDomain.getEventListeners());
            case CONCEPT:
                return Lists.<AbstractDomainElement>newArrayList(documentedDomain.getConcepts());
            case RELATION:
                return Lists.<AbstractDomainElement>newArrayList(documentedDomain.getRelations());
            case REPOSITORY:
                return Lists.<AbstractDomainElement>newArrayList(documentedDomain.getRepositories());
            case SAGA:
                return Lists.<AbstractDomainElement>newArrayList(documentedDomain.getSagas());
            case DOMAIN:
                break;
        }

        return Lists.newArrayList();
    }

}
