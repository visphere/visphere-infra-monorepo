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
    SPHERE_GUILD_DELETED_RESPONSE_SUCCESS("vsph.i18n.sphereGuildDeleted.res.success"),
    SPHERE_GUILD_LINK_CREATED_RESPONSE_SUCCESS("vsph.i18n.sphereGuildLinkCreated.res.success"),
    SPHERE_GUILD_LINK_UPDATED_RESPONSE_SUCCESS("vsph.i18n.sphereGuildLinkUpdated.res.success"),
    SPHERE_GUILD_LINK_DELETED_RESPONSE_SUCCESS("vsph.i18n.sphereGuildLinkDeleted.res.success"),
    SPHERE_TEXT_CHANNEL_CREATED_RESPONSE_SUCCESS("vsph.i18n.sphereTextChannelCreated.res.success"),
    SPHERE_TEXT_CHANNEL_UPDATED_RESPONSE_SUCCESS("vsph.i18n.sphereTextChannelUpdated.res.success"),
    SPHERE_TEXT_CHANNEL_DELETED_RESPONSE_SUCCESS("vsph.i18n.sphereTextChannelDeleted.res.success"),
    SPHERE_GUILD_LEAVE_RESPONSE_SUCCESS("vsph.i18n.sphereGuildLeave.res.success"),
    SPHERE_GUILD_KICK_RESPONSE_SUCCESS("vsph.i18n.sphereGuildKick.res.success"),
    SPHERE_GUILD_BAN_RESPONSE_SUCCESS("vsph.i18n.sphereGuildBan.res.success"),
    SPHERE_GUILD_UNBAN_RESPONSE_SUCCESS("vsph.i18n.sphereGuildUnban.res.success"),
    SPHERE_GUILD_BY_ID_NOT_FOUND_EXCEPTION_MESSAGE("vsph.sphere.exc.sphereGuildByIdNotFound"),
    SPHERE_GUILD_BY_HAS_NO_OWNER_EXCEPTION_MESSAGE("vsph.sphere.exc.sphereGuildHasNoOwner"),
    SPHERE_GUILD_LINK_BY_ID_NOT_FOUND_EXCEPTION_MESSAGE("vsph.sphere.exc.sphereGuildLinkByIdNotFound"),
    SPHERE_GUILD_LINK_INCORRECT_TIME_EXCEPTION_MESSAGE("vsph.sphere.exc.sphereGuildLinkIncorrectTime"),
    SPHERE_TEXT_CHANNEL_BY_ID_NOT_FOUND_EXCEPTION_MESSAGE("vsph.sphere.exc.sphereTextChannelByIdNotFound"),
    SPHERE_TEXT_CHANNEL_DUPLICATE_NAME_EXCEPTION_MESSAGE("vsph.sphere.exc.sphereTextChannelDuplicateName"),
    USER_IS_NOT_GUILD_PARTICIPANT_EXCEPTION_MESSAGE("vsph.sphere.exc.userIsNotGuildParticipant"),
    DELETE_GUIL_OWNER_EXCEPTION_MESSAGE("vsph.sphere.exc.deleteGuildOwner"),
    ;

    private final String holder;
}
