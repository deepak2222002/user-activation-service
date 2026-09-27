package web.minda.project.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import web.minda.project.service.ActivationService;

@RestController
@RequestMapping("/activate")
public class ActivationController {

    private final ActivationService activationService;

    public ActivationController(ActivationService activationService) {
        this.activationService = activationService;
    }

    @GetMapping("/activateAccount")
    public ResponseEntity<String> activateAccount(
            @RequestParam("token") String token) throws MessagingException {

        String result = activationService.activateAccount(token);

        return ResponseEntity.ok(result);
    }
}
