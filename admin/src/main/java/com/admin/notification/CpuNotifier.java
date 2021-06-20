package com.admin.notification;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.notify.AbstractEventNotifier;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Log4j2
public class CpuNotifier extends AbstractEventNotifier {

    protected CpuNotifier(InstanceRepository repository) {
        super(repository);
    }

    @Override
    protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
        log.info("ping event:"+event.getType());
        log.info("ping instance:"+instance.getStatusInfo().getStatus());
        return Mono.fromRunnable(()->{
            if(event instanceof InstanceStatusChangedEvent){
                log.info("instance {}, ({}), is {}", instance.getRegistration().getName(), event.getInstance()
                ,((InstanceStatusChangedEvent) event).getStatusInfo().getStatus());
            }else {
                log.info("instance {}, ({}), type {}", instance.getRegistration().getName(), event.getInstance()
                        , event.getType());
            }
        });
    }
}
