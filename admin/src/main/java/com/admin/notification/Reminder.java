package com.admin.notification;

import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.notify.CompositeNotifier;
import de.codecentric.boot.admin.server.notify.Notifier;
import de.codecentric.boot.admin.server.notify.RemindingNotifier;
import de.codecentric.boot.admin.server.notify.filter.FilteringNotifier;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Configuration(proxyBeanMethods = false)
public class Reminder {

    private final InstanceRepository repository;
    private final ObjectProvider<List<Notifier>> notifiers;

    public Reminder(InstanceRepository repository, ObjectProvider<List<Notifier>> notifiers) {
        this.repository = repository;
        this.notifiers = notifiers;
    }

    @Bean
    public FilteringNotifier filteringNotifier(){
//        List<Notifier> notifierList = new ArrayList<>();
//        notifierList.add(getCpuNotifier());
        CompositeNotifier delegate = new CompositeNotifier(notifiers.getIfAvailable(Collections::emptyList));
        return new FilteringNotifier(delegate, this.repository);
    }

    @Bean
    public CpuNotifier getCpuNotifier(){
        return new CpuNotifier(repository);
    }

    @Primary
    @Bean(initMethod = "start", destroyMethod = "stop")
    public RemindingNotifier remindingNotifier() {
        var remindingNotifier = new RemindingNotifier(filteringNotifier(), repository);
        remindingNotifier.setReminderPeriod(Duration.ofMinutes(5));
        remindingNotifier.setCheckReminderInverval(Duration.ofSeconds(10));
        log.info("Notifier Configuration OK");
        return remindingNotifier;
    }
}
