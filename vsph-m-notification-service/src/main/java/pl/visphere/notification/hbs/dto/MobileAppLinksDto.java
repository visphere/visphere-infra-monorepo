/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.notification.hbs.dto;

public record MobileAppLinksDto(
    String appGalleryLink,
    String appStoreLink,
    String googlePlayLink
) {
}
