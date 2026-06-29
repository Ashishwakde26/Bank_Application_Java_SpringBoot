package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.api.controllers.OpenAccountController;
import com.techbank.cqrs.core.commands.BaseCommand;
import com.techbank.cqrs.core.commands.CommandHandlerMethod;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class AccountCommandDispatcher implements CommandDispatcher {

    private final Logger logger = Logger.getLogger(AccountCommandDispatcher.class.getName());
    private final Map<Class<? extends BaseCommand>, List<CommandHandlerMethod>> routes = new HashMap<>();

    @Override
    public <T extends BaseCommand> void registerHandler(Class<T> type, CommandHandlerMethod<T> handler) {
        var handlers = routes.computeIfAbsent(type, c -> new LinkedList<>());
        handlers.add(handler);

        // Log handler registration details
        logger.info("Registered handler: " + handler.getClass().getName()
                + " | Method: " + handler
                + " | For command type: " + type.getName()
                + " | Total handlers for this type: " + handlers.size());
    }

    @Override
    public void send(BaseCommand command) {
        logger.info("Sending command: " + command.getClass().getName());
        var handlers = routes.get(command.getClass());
        logger.info("Handlers found for command [" + command.getClass().getName() + "]: " + handlers);
        if (handlers == null || handlers.isEmpty()) {
            logger.severe("No handler registered for command: " + command.getClass().getName());
            throw new RuntimeException("No command handler was registered!");
        }
        if (handlers.size() > 1) {
            throw new RuntimeException("Cannot send command to more than one handler!");
        }
        handlers.get(0).handle(command);
    }
}
