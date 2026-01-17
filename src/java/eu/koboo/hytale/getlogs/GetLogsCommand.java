package eu.koboo.hytale.getlogs;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.OpenChatWithCommand;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public class GetLogsCommand extends CommandBase {

    private final GetLogsPlugin plugin;

    public GetLogsCommand(GetLogsPlugin plugin) {
        super("getlogs", "Creates a paste-link with the latest log files content.");
        this.plugin = plugin;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext commandContext) {
        CommandSender sender = commandContext.sender();
        File logDirectory = new File("logs/");
        if(!logDirectory.exists()) {
            sender.sendMessage(Message.raw("Directory \"logs/\" doesn't exist."));
            return;
        }
        File[] logFiles = logDirectory.listFiles();
        if(logFiles == null) {
            sender.sendMessage(Message.raw("No files in directory \"logs/\" found."));
            return;
        }

        File newestFile = logFiles[0];
        for (File logFile : logFiles) {
            if (logFile.lastModified() > newestFile.lastModified()) {
                newestFile = logFile;
            }
        }
        String fileContent;
        try {
            fileContent = Files.readString(newestFile.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            sender.sendMessage(Message.raw("Error occurred while reading log file:"));
            sender.sendMessage(Message.raw("File: " + newestFile.getName()));
            sender.sendMessage(Message.raw("Error: " + e.getMessage()));
            return;
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.pastes.dev/post"))
                .POST(HttpRequest.BodyPublishers.ofString(fileContent, StandardCharsets.UTF_8))
                .header("Content-Type", "text/log")
                .header("User-Agent", "Hytale-GetLogs (github.com/Koboo/hytale-getlogs)")
                .build();

        HttpResponse<String> response;
        try {
            response = plugin.client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException | InterruptedException e) {
            sender.sendMessage(Message.raw("Error occurred while posting log file:"));
            sender.sendMessage(Message.raw("File: " + newestFile.getName()));
            sender.sendMessage(Message.raw("Error: " + e.getMessage()));
            return;
        }
        int statusCode = response.statusCode();
        if (statusCode != 201) {
            sender.sendMessage(Message.raw("Received invalid statusCode \"" + statusCode + "\" from paste."));
            return;
        }
        Optional<String> optionalPasteKey = response.headers().firstValue("Location");
        if(optionalPasteKey.isEmpty()) {
            sender.sendMessage(Message.raw("Couldn't decode paste response. No pasteKey in headers."));
            return;
        }
        String pasteKey = optionalPasteKey.get();
        String pasteUrl = "https://pastes.dev/" + pasteKey;
        sender.sendMessage(Message.raw("Paste-Link: " + pasteUrl));
        if(commandContext.isPlayer()) {
            World world = commandContext.senderAs(Player.class).getWorld();
            if(world == null) {
                return;
            }
            world.execute(() -> {
                Ref<EntityStore> ref = commandContext.senderAsPlayerRef();
                if(ref == null) {
                    return;
                }
                PlayerRef playerRef = ref.getStore().getComponent(ref, PlayerRef.getComponentType());
                if(playerRef == null) {
                    return;
                }
                playerRef.getPacketHandler().write(new OpenChatWithCommand(pasteUrl));
            });
        }
    }
}
