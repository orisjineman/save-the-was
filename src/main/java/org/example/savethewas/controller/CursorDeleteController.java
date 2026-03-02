package org.example.savethewas.controller;

import lombok.RequiredArgsConstructor;
import org.example.savethewas.service.CursorDeleteService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class CursorDeleteController {
    private final CursorDeleteService cursorDeleteService;

    @PostMapping("/cursor-delete")
    public CursorDeleteService.CursorDeleteResult cursorDelete(
            @RequestParam String region,
            @RequestParam(defaultValue = "1000") int chunkSize
    ) throws IOException {
        return cursorDeleteService.deleteMenusWithCursor(region, chunkSize);
    }
}