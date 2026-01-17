package eu.koboo.hytale.getlogs;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;
import java.net.http.HttpClient;

public class GetLogsPlugin extends JavaPlugin{

    public final HttpClient client;

    public GetLogsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        client = HttpClient.newBuilder().build();
    }

    @Override
    protected void setup() {
        getCommandRegistry().registerCommand(new GetLogsCommand(this));
        super.setup();
    }

    @Override
    protected void shutdown() {
        if(client != null) {
            client.close();
            client.shutdownNow();
        }
        super.shutdown();
    }
}
