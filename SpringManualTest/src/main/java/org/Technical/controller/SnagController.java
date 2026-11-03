
package org.Technical.controller;

import org.Technical.dto.SnagRequest;
import org.Technical.dto.SnagResponse;
import org.Technical.service.SnagTroubleshootingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/snags")
public class SnagController {

    private final SnagTroubleshootingService troubleshootingService;

    public SnagController(
            SnagTroubleshootingService troubleshootingService) {
        this.troubleshootingService = troubleshootingService;
    }

    @PostMapping("/troubleshoot")
    public SnagResponse troubleshoot(
            @Valid @RequestBody SnagRequest request) {
        return troubleshootingService.troubleshoot(request);
    }
}
