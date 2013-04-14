// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.axonframework.eventhandling.annotation.EventHandler;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.ddd.IAggregateRoot;
import com.viadeo.kasper.doc.KasperLibrary;
import com.viadeo.kasper.er.IConcept;
import com.viadeo.kasper.er.annotation.XKasperConcept;

public final class DocumentedConcept extends DocumentedEntity {
	private static final long serialVersionUID = 3750351443738850009L;
	
	static final public String TYPE_NAME = "concept";
	static final public String PLURAL_TYPE_NAME = "concepts";
	
	final private List<String> sourceEvents = Lists.newArrayList(); 
	
	// ------------------------------------------------------------------------
	
	DocumentedConcept(final KasperLibrary kl) { // Used as empty concept to populate
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
	}
	
	public DocumentedConcept(final KasperLibrary kl, final Class<? extends IConcept> conceptClazz) {
		super(kl, TYPE_NAME, PLURAL_TYPE_NAME);
		
		final XKasperConcept annotation = conceptClazz.getAnnotation(XKasperConcept.class);
		
		// Find if it's an aggregate ------------------------------------------
		final boolean isAggregate = IAggregateRoot.class.isAssignableFrom(conceptClazz);
		
		// Find associated domain ---------------------------------------------
		final Class<? extends IDomain> domain = annotation.domain();
		
		// Get description ----------------------------------------------------
		String description = annotation.description();
		if (description.isEmpty()) {
			description = String.format("The %s concept", conceptClazz.getSimpleName().replaceAll("Concept", ""));
		}
		
		// Set properties -----------------------------------------------------
		this.setIsAggregate(isAggregate);
		this.setLabel(annotation.label());
		this.setName(conceptClazz.getSimpleName());
		this.setDomainName(domain.getSimpleName());
		this.setDescription(description);
		
		if (isAggregate) {
			fillSourceEvents(conceptClazz);
		} else {
			fillParent(conceptClazz);
		}
		
		fillProperties(conceptClazz);
	}	
	
	// --
	
	private void fillSourceEvents(final Class<? extends IConcept> conceptClazz) {
		final Method[] methods = conceptClazz.getDeclaredMethods();
		for (Method method : methods) {
			if (null != method.getAnnotation(EventHandler.class)) {
				final Class<?>[] types = method.getParameterTypes();
				if (types.length == 1) {
					sourceEvents.add(types[0].getSimpleName());
				}
			}
		}
	}
	
	// ------------------------------------------------------------------------
	
	public Collection<DocumentedNode> getSourceRelations() {
		final KasperLibrary kl = this.getKasperLibrary();
		return kl.simpleNodesFrom(kl.getSourceConceptRelations(getName())).values();
	}
	
	public Collection<DocumentedNode> getTargetRelations() {
		final KasperLibrary kl = this.getKasperLibrary();
		return kl.simpleNodesFrom(kl.getTargetConceptRelations(getName())).values();
	}
	
	// ------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public Collection<DocumentedNode> getSourceEvents() {		
		if (this.sourceEvents.size() == 0) {
			return Collections.EMPTY_LIST;
		}
		
		final KasperLibrary kl = this.getKasperLibrary();
		final List<DocumentedEvent> events = Lists.newArrayList();
		for (final String eventName : this.sourceEvents) {
			final Optional<DocumentedEvent> event = kl.getEvent(getDomainName(), eventName);
			if (event.isPresent()) {
				events.add(event.get());
			}
		}
		
		return kl.simpleNodesFrom(events).values();
	}
	
}