/*
 * Copyright (c) 2023 by Visphere & Vsph Technologies
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.visphere.sphere.domain.guild;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pl.visphere.lib.AbstractAuditableEntity;
import pl.visphere.sphere.domain.banneduser.BannedUserEntity;
import pl.visphere.sphere.domain.guildlink.GuildLinkEntity;
import pl.visphere.sphere.domain.textchannel.TextChannelEntity;
import pl.visphere.sphere.domain.userguild.UserGuildEntity;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

@Entity
@Table(name = "guilds")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuildEntity extends AbstractAuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;

    @Enumerated(EnumType.STRING)
    private GuildCategory category;

    private Boolean isPrivate;

    private Long ownerId;

    @OneToMany(cascade = { PERSIST, MERGE }, mappedBy = "guild")
    private Set<TextChannelEntity> textChannels = new HashSet<>();

    @OneToMany(cascade = { PERSIST, MERGE }, mappedBy = "guild")
    private Set<UserGuildEntity> userGuilds = new HashSet<>();

    @OneToMany(cascade = { PERSIST, MERGE }, mappedBy = "guild")
    private Set<GuildLinkEntity> guildLinks = new HashSet<>();

    @OneToMany(cascade = { PERSIST, MERGE }, mappedBy = "guild")
    private Set<BannedUserEntity> bannedUsers = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GuildCategory getCategory() {
        return category;
    }

    public void setCategory(GuildCategory category) {
        this.category = category;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    Set<TextChannelEntity> getTextChannels() {
        return textChannels;
    }

    void setTextChannels(Set<TextChannelEntity> textChannels) {
        this.textChannels = textChannels;
    }

    Set<UserGuildEntity> getUserGuilds() {
        return userGuilds;
    }

    void setUserGuilds(Set<UserGuildEntity> userGuilds) {
        this.userGuilds = userGuilds;
    }

    Set<GuildLinkEntity> getGuildLinks() {
        return guildLinks;
    }

    void setGuildLinks(Set<GuildLinkEntity> guildLinks) {
        this.guildLinks = guildLinks;
    }

    Set<BannedUserEntity> getBannedUsers() {
        return bannedUsers;
    }

    void setBannedUsers(Set<BannedUserEntity> bannedUsers) {
        this.bannedUsers = bannedUsers;
    }

    public void persistTextChannel(TextChannelEntity textChannel) {
        textChannels.add(textChannel);
        textChannel.setGuild(this);
    }

    public void persistUserGuild(UserGuildEntity userGuild) {
        userGuilds.add(userGuild);
        userGuild.setGuild(this);
    }

    public void persistGuildLink(GuildLinkEntity guildLink) {
        guildLinks.add(guildLink);
        guildLink.setGuild(this);
    }

    public void persistBannedUser(BannedUserEntity bannedUser) {
        bannedUsers.add(bannedUser);
        bannedUser.setGuild(this);
    }

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            ", category=" + category +
            ", isPrivate=" + isPrivate +
            ", ownerId=" + ownerId +
            '}';
    }
}
