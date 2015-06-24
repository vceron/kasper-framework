// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.viadeo.kasper.event.annotation.XKasperSaga;

import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.checkNotNull;

public class SchedulingFacetApplier implements FacetApplier {

    private final Scheduler scheduler;

    public SchedulingFacetApplier(final Scheduler scheduler) {
        this.scheduler = checkNotNull(scheduler);
    }

    @Override
    public Step apply(Method method, Step step) {
        XKasperSaga.Schedule scheduleAnnotation = method.getAnnotation(XKasperSaga.Schedule.class);
        XKasperSaga.CancelSchedule cancelScheduleAnnotation = method.getAnnotation(XKasperSaga.CancelSchedule.class);

        if (scheduleAnnotation != null && cancelScheduleAnnotation != null) {
            throw new IllegalStateException(String.format("Should have one schedule annotation per step : %s", step.getSagaClass().getName()));
        }

        if (scheduleAnnotation != null) {
            return new SchedulingStep(scheduler, step, scheduleAnnotation);
        }

        if (cancelScheduleAnnotation != null) {
            return new SchedulingStep(scheduler, step, cancelScheduleAnnotation);
        }

        return step;
    }

    @Override
    public int compareTo(FacetApplier o) {
        return 0;
    }
}
