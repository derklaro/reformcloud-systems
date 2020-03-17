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
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.bot.BotConnectionHandler;
import systems.reformcloud.util.FileUtils;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Represents a default implementation of the {@link BotConnectionHandler} for the discord env.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class DiscordConnectionHandler implements BotConnectionHandler<JDA> {

    private static final Path CONFIG = Paths.get("config.properties");

    @Nullable
    @Override
    public JDA connect() {
        try {
            return JDABuilder
                    .createDefault(Preconditions.checkNotNull(this.readToken(), "Unable to read discord token"))
                    .setAutoReconnect(true)
                    .setEnableShutdownHook(true)
                    .setStatus(OnlineStatus.ONLINE)
                    .build()
                    .awaitReady();
        } catch (final LoginException | InterruptedException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Nullable
    private String readToken() {
        if (System.getProperties().containsKey("discord-token")) {
            return System.getProperty("discord-token");
        }

        Properties properties = new Properties();

        if (Files.notExists(CONFIG)) {
            FileUtils.createNewFile(CONFIG);
            properties.setProperty("discord-token", "null");

            try (OutputStream stream = Files.newOutputStream(CONFIG)) {
                properties.store(stream, "default configuration file");
            } catch (final IOException ex) {
                ex.printStackTrace();
            }

            return null;
        }

        try (InputStream stream = Files.newInputStream(CONFIG)) {
            properties.load(stream);
            return properties.getProperty("discord-token");
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
