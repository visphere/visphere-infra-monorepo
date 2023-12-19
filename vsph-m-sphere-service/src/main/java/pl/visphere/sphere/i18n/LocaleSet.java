/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.i18n;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.visphere.lib.i18n.LocaleExtendableSet;

@Getter
@RequiredArgsConstructor
public enum LocaleSet implements LocaleExtendableSet {
    SPHERE_GUILD_CREATED_RESPONSE_SUCCESS("vsph.i18n.sphereGuildCreated.res.success"),
    SPHERE_GUILD_UPDATE_RESPONSE_SUCCESS("vsph.i18n.sphereGuildUpdate.res.success"),
    SPHERE_GUILD_UPDATE_VISIBILITY_RESPONSE_SUCCESS("vsph.i18n.sphereGuildUpdateVisibility.res.success"),
    SPHERE_GUILD_UPDATE_VISIBILITY_WITH_REMOVE_LINKS_RESPONSE_SUCCESS("vsph.i18n.sphereGuildUpdateVisibilityWithRemovedLinks.res.success"),
    SPHERE_GUILD_LINK_CREATED_RESPONSE_SUCCESS("vsph.i18n.sphereGuildLinkCreated.res.success"),
    SPHERE_GUILD_LINK_UPDATED_RESPONSE_SUCCESS("vsph.i18n.sphereGuildLinkUpdated.res.success"),
    SPHERE_GUILD_LINK_DELETED_RESPONSE_SUCCESS("vsph.i18n.sphereGuildLinkDeleted.res.success"),
    //
    SPHERE_TEXT_CHANNEL_CREATED_RESPONSE_SUCCESS("vsph.i18n.sphereTextChannelCreated.res.success"),
    SPHERE_TEXT_CHANNEL_UPDATED_RESPONSE_SUCCESS("vsph.i18n.sphereTextChannelUpdated.res.success"),
    SPHERE_TEXT_CHANNEL_DELETED_RESPONSE_SUCCESS("vsph.i18n.sphereTextChannelDeleted.res.success"),
    SPHERE_GUILD_BY_ID_NOT_FOUND_EXCEPTION_MESSAGE("vsph.sphere.exc.sphereGuildByIdNotFound"),
    SPHERE_GUILD_LINK_BY_ID_NOT_FOUND_EXCEPTION_MESSAGE("vsph.sphere.exc.sphereGuildLinkByIdNotFound"),
    SPHERE_GUILD_LINK_INCORRECT_TIME_EXCEPTION_MESSAGE("vsph.sphere.exc.sphereGuildLinkIncorrectTime");
    SPHERE_TEXT_CHANNEL_BY_ID_NOT_FOUND_EXCEPTION_MESSAGE("vsph.sphere.exc.sphereTextChannelByIdNotFound"),
    SPHERE_TEXT_CHANNEL_DUPLICATE_NAME_EXCEPTION_MESSAGE("vsph.sphere.exc.sphereTextChannelDuplicateName"),

    private final String holder;
}
