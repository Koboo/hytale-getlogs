package eu.koboo.hytale.getlogs;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

public class GetLogsPlugin extends JavaPlugin{

    public GetLogsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        getCommandRegistry().registerCommand(new GetLogsCommand(this));
        super.setup();
    }

    @Override
    protected void shutdown() {
        super.shutdown();
    }
}
