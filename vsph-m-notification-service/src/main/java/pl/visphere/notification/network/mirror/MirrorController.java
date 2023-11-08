/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.network.mirror;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.visphere.notification.network.mirror.dto.MirrorMailReqDto;

@RestController
@RequestMapping("/api/v1/notification/mail/mirror")
@RequiredArgsConstructor
class MirrorController {
    private final MirrorService mirrorService;

    @PostMapping(value = "/raw", produces = MediaType.TEXT_HTML_VALUE)
    ResponseEntity<String> extractMirrorMail(@Valid @RequestBody MirrorMailReqDto reqDto) {
        return ResponseEntity.ok(mirrorService.extractMirrorMail(reqDto));
    }
}
