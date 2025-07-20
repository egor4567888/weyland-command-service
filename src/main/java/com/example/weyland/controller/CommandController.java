package com.example.weyland.controller;

import com.example.weyland.audit.WeylandWatchingYou;
import com.example.weyland.dto.CommandRequest;
import com.example.weyland.service.CommandService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/commands")

public class CommandController {
    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping
    @WeylandWatchingYou
    public ResponseEntity<?> receiveCommand(@RequestBody @Valid CommandRequest command) {
        commandService.handleCommand(command);
        return ResponseEntity.ok().body("{\"status\":\"ACCEPTED\"}");
    }
}