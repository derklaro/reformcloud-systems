/*
 * This file is licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 Pasqual Koschmieder.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package systems.reformcloud.discord;

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.api.GlobalAPI;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.bot.BotConnectionHandler;
import systems.reformcloud.bot.feature.BotFeature;
import systems.reformcloud.discord.command.commands.*;
import systems.reformcloud.discord.features.CommandHandlerFeature;
import systems.reformcloud.discord.features.antibot.AntiSelfBotFeature;
import systems.reformcloud.discord.features.logger.LoggerFeature;
import systems.reformcloud.discord.listener.DiscordUserJoinListener;
import systems.reformcloud.discord.listener.DiscordWarnCreateListener;
import systems.reformcloud.discord.punishments.listener.PunishmentCreateListener;
import systems.reformcloud.discord.punishments.listener.PunishmentRevokeListener;
import systems.reformcloud.discord.user.DiscordUserManagement;
import systems.reformcloud.user.UserManagement;
import systems.reformcloud.user.punish.DefaultPunishmentTypes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * A basic implementation of a {@link Bot} for the discord env.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class DiscordBot implements Bot<JDA> {

    private JDA jda;

    private UserManagement userManagement;

    @Override
    public void doConnect(@NotNull BotConnectionHandler<JDA> connectionHandler) {
        this.jda = Preconditions.checkNotNull(connectionHandler.connect(), "Unable to connect to discord web host");
        this.userManagement = new DiscordUserManagement();

        DiscordUtil.init(this.jda);
        DiscordUtil.getGuild().retrieveMembers().join();
        DiscordUtil.getGuild().getMembers().forEach(e -> {
            var user = this.userManagement.getUserOrCreate(e.getIdLong());
            if (e.getRoles().size() == 0) {
                user.getPunishments()
                        .stream()
                        .filter(p -> p.getPunishmentType().equals(DefaultPunishmentTypes.BAN.name()))
                        .findAny()
                        .ifPresentOrElse(p -> DiscordUtil.getGuild().ban(e, 0, p.getReason()).queue(), () -> {
                            if (user.getPunishments().size() > 0 && user.getPunishments()
                                    .stream()
                                    .anyMatch(p -> p.getPunishmentType().equals(DefaultPunishmentTypes.MUTE.name()))) {
                                DiscordUtil.getGuild().addRoleToMember(e, DiscordUtil.getPunishedRole()).queue();
                            }

                            DiscordUtil.getGuild().addRoleToMember(e, DiscordUtil.getMemberRole()).queue();
                        });
            }
        });

        this.init(Arrays.asList(
                new CommandHandlerFeature(),
                new LoggerFeature(),
                new AntiSelfBotFeature()
        ));

        GlobalAPI.getEventManager().registerListener(new PunishmentRevokeListener(this));
        GlobalAPI.getEventManager().registerListener(new PunishmentCreateListener(this));
        GlobalAPI.getEventManager().registerListener(new DiscordWarnCreateListener());
        GlobalAPI.getEventManager().registerListener(new DiscordUserJoinListener());

        GlobalAPI.getCommandMap().registerCommand(new WarnCommand(this));
        GlobalAPI.getCommandMap().registerCommand(new ListWarnsCommand(this));
        GlobalAPI.getCommandMap().registerCommand(new DeleteWarnCommand(this));
        GlobalAPI.getCommandMap().registerCommand(new PunishCommand(this));
        GlobalAPI.getCommandMap().registerCommand(new ListPunishmentsCommand(this));
        GlobalAPI.getCommandMap().registerCommand(new DeletePunishCommand(this));
        GlobalAPI.getCommandMap().registerCommand(new PingCommand(this));
        GlobalAPI.getCommandMap().registerCommand(new UptimeCommand(this));
        GlobalAPI.getCommandMap().registerCommand(new GradleCommand(this));
        GlobalAPI.getCommandMap().registerCommand(new BuildGradleCommand(this));
        GlobalAPI.getCommandMap().registerCommand(new MavenCommand(this));
        GlobalAPI.getCommandMap().registerCommand(new BuildMavenCommand(this));
        GlobalAPI.getCommandMap().registerCommand(new HelpCommand(this));
        GlobalAPI.getCommandMap().registerCommand(new UserInfoCommand(this));
        GlobalAPI.getCommandMap().registerCommand(new DramaCommand(this));
    }

    @Override
    public void init(@NotNull Collection<BotFeature<JDA>> botFeatures) {
        if (this.jda == null) {
            throw new IllegalStateException("JDA is not initialized yet");
        }

        botFeatures
                .stream()
                .filter(e -> e.isApplicableTo(this))
                .forEach(e -> e.handleStart(this));
    }

    @Override
    public @NotNull
    UserManagement getAssociatedUserManagement() {
        return Preconditions.checkNotNull(this.userManagement, "JDA is not initialized yet");
    }

    @Override
    public boolean isConnected() {
        return this.jda != null && this.jda.getStatus().equals(JDA.Status.CONNECTED);
    }

    @Override
    public void shutdownNow() {
        if (this.jda != null) {
            this.jda.shutdownNow();
            this.jda = null;
        }

        if (this.userManagement != null) {
            this.userManagement.flushAndClose();
            this.userManagement = null;
        }
    }

    @NotNull
    @Override
    public Optional<JDA> getCurrentInstance() {
        return Optional.ofNullable(this.jda);
    }
}
