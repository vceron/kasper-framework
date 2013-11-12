// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.google.common.base.Optional;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.doc.KasperLibrary;

import java.util.Collection;

public final class DocumentedDomain extends DocumentedNode {
	private static final long serialVersionUID = 3888726543821083680L;
	
	public static final String TYPE_NAME = "domain";
	public static final String PLURAL_TYPE_NAME = "domains";
	
	private String prefix;
	private String parent;
	
	// ------------------------------------------------------------------------
	
	public DocumentedDomain(final KasperLibrary kl, final Class<? extends Domain> domainClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);		

        final DomainResolver resolver = this.getKasperLibrary().getResolverFactory().getDomainResolver();

        this.prefix = resolver.getPrefix(domainClazz);
        this.setLabel(resolver.getLabel(domainClazz));
        this.setDescription(resolver.getDescription(domainClazz));
        this.setName(domainClazz.getSimpleName());
		
		final Class parentClazz = domainClazz.getSuperclass();
		if (null != parentClazz) {
			this.parent = parentClazz.getSimpleName();
		}
	}
	
	// ------------------------------------------------------------------------

	public String getPrefix() {
		return this.prefix;
	}	
	
	public DocumentedNode getParent() {
		final KasperLibrary kl = this.getKasperLibrary();
		if (null != this.parent) {
			final Optional<DocumentedDomain> domainParent = kl.getDomainFromName(this.parent);
			if (domainParent.isPresent()) {
				return kl.getSimpleNodeFrom(domainParent.get());
			}
		}
		return null;
	}	
	
	public Collection<DocumentedNode> getCommands() {
		final KasperLibrary kl = this.getKasperLibrary();
		return kl.simpleNodesFrom( kl.getCommands( getName())).values();
	}

	public Collection<DocumentedNode> getConcepts() {
		final KasperLibrary kl = this.getKasperLibrary();
		return kl.simpleNodesFrom( kl.getConcepts(getName()) ).values();
	}
	
	public Collection<DocumentedNode> getRelations() {
		final KasperLibrary kl = this.getKasperLibrary();
		return kl.simpleNodesFrom( kl.getRelations(getName()) ).values();
	}
	
	public Collection<DocumentedNode> getEvents() {
		final KasperLibrary kl = this.getKasperLibrary();
		return kl.simpleNodesFrom( kl.getEvents(getName()) ).values();
	}
	
	public Collection<DocumentedNode> getRepositories() {
		final KasperLibrary kl = this.getKasperLibrary();
		return kl.simpleNodesFrom( kl.getRepositories(getName()) ).values();
	}
	
	public Collection<DocumentedNode> getListeners() {
		final KasperLibrary kl = this.getKasperLibrary();
		return kl.simpleNodesFrom( kl.getListeners(getName()) ).values();
	}
	
	public Collection<DocumentedNode> getHandlers() {
		final KasperLibrary kl = this.getKasperLibrary();
		return kl.simpleNodesFrom( kl.getHandlers(getName()) ).values();
	}
	
	public Collection<DocumentedNode> getQueryHandlers() {
		final KasperLibrary kl = this.getKasperLibrary();
		return kl.simpleNodesFrom( kl.getQueryHandlers(getName()) ).values();
	}

    public Collection<DocumentedNode> getQueries(){
        final KasperLibrary kl=this.getKasperLibrary();
        return kl.simpleNodesFrom( kl.getQueries(getName()) ).values();
    }

    public Collection<DocumentedNode> getQueryResults(){
        final KasperLibrary kl=this.getKasperLibrary();
        return kl.simpleNodesFrom( kl.getQueryResults(getName()) ).values();
    }

}
