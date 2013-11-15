package com.viadeo.kasper.test.timelines.entities;

import com.viadeo.kasper.er.ComponentConcept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.test.timelines.Timelines;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.eventsourcing.AbstractEventSourcedAggregateRoot;

@XKasperConcept(domain = Timelines.class, label = Media.NAME)
public class Media implements ComponentConcept<Status> {
	private static final long serialVersionUID = -5482616251141907946L;

	public static final String NAME = "StatusMedia";
	
	@SuppressWarnings("rawtypes")
	@Override
	public void handleRecursively(DomainEventMessage arg0) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void registerAggregateRoot(AbstractEventSourcedAggregateRoot arg0) {
		// TODO Auto-generated method stub
		
	}

}
