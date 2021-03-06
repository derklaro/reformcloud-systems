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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.config.ConfigUtil;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The discord punishment util
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class DiscordUtil {

    private DiscordUtil() {
        throw new UnsupportedOperationException();
    }

    private static Role punishedRole;

    private static Role memberRole;

    private static TextChannel loggingChannel;

    private static TextChannel terminalChannel;

    private static TextChannel informationChannel;

    private static TextChannel botCommandsChannel;

    private static Guild guild;

    private static String captchaRequestMessage;

    private static int firstAutoMuteWarnCount;

    private static int secondAutoMuteWarnCount;

    private static int autoBanWarnCount;

    public static void init(@NotNull JDA parent) {
        guild = checkNotNull(parent.getGuildById(getNonNull("discord-guild")), "Unable to find guild");

        punishedRole = checkNotNull(guild.getRoleById(getNonNull("discord-punish-role")),
                "Unable to find punished role");

        memberRole = checkNotNull(guild.getRoleById(getNonNull("discord-member-role")),
                "Unable to find member role");

        loggingChannel = checkNotNull(guild.getTextChannelById(getNonNull("discord-log-channel")),
                "Unable to find logging text channel");

        terminalChannel = checkNotNull(guild.getTextChannelById(getNonNull("discord-terminal-channel")),
                "Unable to find terminal text channel");

        botCommandsChannel = checkNotNull(guild.getTextChannelById(getNonNull("discord-bot-command-channel")),
                "Unable to find bot commands text channel");

        informationChannel = checkNotNull(guild.getTextChannelById(getNonNull("discord-info-channel")),
                "Unable to find information text channel");

        captchaRequestMessage = getNonNull("discord-captcha-request-message");
        firstAutoMuteWarnCount = getInteger("discord-auto-mute-first");
        secondAutoMuteWarnCount = getInteger("discord-auto-mute-second");
        autoBanWarnCount = getInteger("discord-auto-ban");
    }

    @NotNull
    public static Role getPunishedRole() {
        return punishedRole;
    }

    @NotNull
    public static Role getMemberRole() {
        return memberRole;
    }

    @NotNull
    public static TextChannel getLoggingChannel() {
        return loggingChannel;
    }

    @NotNull
    public static TextChannel getTerminalChannel() {
        return terminalChannel;
    }

    @NotNull
    public static TextChannel getBotCommandsChannel() {
        return botCommandsChannel;
    }

    @NotNull
    public static TextChannel getInformationChannel() {
        return informationChannel;
    }

    @NotNull
    public static Guild getGuild() {
        return guild;
    }

    @NotNull
    public static String getCaptchaRequestMessage() {
        return captchaRequestMessage;
    }

    public static int getFirstAutoMuteWarnCount() {
        return firstAutoMuteWarnCount;
    }

    public static int getSecondAutoMuteWarnCount() {
        return secondAutoMuteWarnCount;
    }

    public static int getAutoBanWarnCount() {
        return autoBanWarnCount;
    }

    @NotNull
    private static String getNonNull(String path) {
        return checkNotNull(ConfigUtil.parseProperties().getProperty(path));
    }

    private static int getInteger(String path) {
        return Integer.parseInt(getNonNull(path));
    }
}
