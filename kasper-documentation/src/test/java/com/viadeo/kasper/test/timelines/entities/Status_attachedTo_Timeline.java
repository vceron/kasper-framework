package com.viadeo.kasper.test.timelines.entities;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.er.ComponentRelation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.test.timelines.Timelines;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.eventsourcing.AbstractEventSourcedAggregateRoot;

@XKasperRelation(domain = Timelines.class, label = Status_attachedTo_Timeline.NAME, description = Status_attachedTo_Timeline.DESCRIPTION)
public class Status_attachedTo_Timeline implements ComponentRelation<Status, Timeline> {
	private static final long serialVersionUID = 7402161661624425018L;

	public static final String NAME = "attached_to";
	public static final String DESCRIPTION = "A status is attached to one timeline";
	
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

	@Override
	public KasperID getSourceIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KasperID getTargetIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBidirectional() {
		// TODO Auto-generated method stub
		return false;
	}

}
