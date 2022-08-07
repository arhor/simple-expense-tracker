package com.github.arhor.simple.expense.tracker.service.event;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.github.arhor.simple.expense.tracker.service.task.startup.StartupTask;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class StartupTaskExecutor implements ApplicationListener<ContextRefreshedEvent> {

    protected final List<StartupTask> startupTasks;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        for (val task : startupTasks) {
            task.execute();
        }
    }
}
