/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.network.mirror;

import pl.visphere.notification.network.mirror.dto.MirrorMailReqDto;

interface MirrorService {
    String extractMirrorMail(MirrorMailReqDto reqDto);
}
