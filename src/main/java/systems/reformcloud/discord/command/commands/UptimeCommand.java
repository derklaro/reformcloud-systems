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
package systems.reformcloud.discord.command.commands;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.commands.source.CommandSource;
import systems.reformcloud.discord.command.BasicDiscordCommand;
import systems.reformcloud.util.StringUtils;

import java.lang.management.ManagementFactory;

/**
 * Sends the uptime of the bot to the discord server
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class UptimeCommand extends BasicDiscordCommand {

    public UptimeCommand(@NotNull Bot<JDA> parent) {
        super(parent, "!uptime", new String[0], "Shows the uptime of the discord bot");
    }

    @Override
    public void execute(@NotNull CommandSource source, @NotNull String commandLine, @NotNull String[] strings) {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();

        long years = uptime / 31104000000L;
        long months = uptime / 2592000000L % 12;
        long days = uptime / 86400000L % 30;
        long hours = uptime / 3600000L % 24;
        long minutes = uptime / 60000L % 60;
        long seconds = uptime / 1000L % 60;

        var time = "";
        time += years == 0 ? "" : years + " year" + (years > 1 ? "s" : "") + ", ";
        time += months == 0 ? "" : months + " month" + (months > 1 ? "s" : "") + ", ";
        time += days == 0 ? "" : days + " day" + (days > 1 ? "s" : "") + ", ";
        time += hours == 0 ? "" : hours + " hour" + (hours > 1 ? "s" : "") + ", ";
        time += minutes == 0 ? "" : minutes + " minute" + (minutes > 1 ? "s" : "") + ", ";
        time += seconds == 0 ? "" : seconds + " second" + (seconds > 1 ? "s" : "") + ", ";

        time = StringUtils.replaceLast(time, ", ", "");
        time = StringUtils.replaceLast(time, ",", " and");

        source.sendMessage("I am running for " + time);
    }
}
