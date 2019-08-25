package red.mohist.configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import red.mohist.api.ServerAPI;
import red.mohist.command.defaultcomamnd.DumpCommand;
import red.mohist.command.defaultcomamnd.ItemCommand;
import red.mohist.command.defaultcomamnd.MohistCommand;
import red.mohist.command.defaultcomamnd.VersionCommand;
import red.mohist.i18n.Message;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class MohistConfig extends ConfigBase{

    private final String HEADER = "This is the main configuration file for Mohist.\n"
            + "\n"
            + "Home: https://www.mohist.red/\n";

    public static MohistConfig instance;

    /* ======================================================================== */

    public final StringSetting unknownCommandMessage = new StringSetting(this, "messages.use-unknow-command", Message.getString("use.unknow.command"), "Prompt unknown command");
    public final StringSetting outdatedClientMessage = new StringSetting(this, "messages.Outdate-Client", Message.getString("outdate.client"), "Outdate Client");
    public final StringSetting outdatedServerMessage = new StringSetting(this, "messages.Outdate-Server", Message.getString("outdate.server"), "Outdate Server");

    public final BoolSetting printThreadTimeCost = new BoolSetting(this, "debug.printThreadTimeCost", false, "printThreadTimeCost");
    public final BoolSetting dumpRemapPluginClass = new BoolSetting(this, "remap.dumpRemapPluginClass", false, "dumpRemapPluginClass");
    public final BoolSetting printRemapPluginClass = new BoolSetting(this, "remap.printRemapPluginClass", false, "printRemapPluginClass");
    public final BoolSetting printInvalidMapping = new BoolSetting(this, "debug.printInvalidMapping", false, "printInvalidMapping");
    public final BoolSetting nmsRemap = new BoolSetting(this, "remap.nmsRemap", true, "Compatible with nms plugin");
    public final BoolSetting reflectRemap = new BoolSetting(this, "remap.reflectRemap", true, "Compatible reflection plugin");

    public final BoolSetting multiVersionRemap = new BoolSetting(this, "remap.multiVersionRemap", false, "Compatible with multiple versions of nms");
    public final StringArraySetting multiVersionRemapPlugins = new StringArraySetting(this, "remap.multiVersionRemapPlugins", "", "Need a list of compatible plugins");

    public final IntSetting entityCollideFrequency = new IntSetting(this, "perfomance.entityCollideFrequency",2,"Entity Collide Frequency");
    public final IntSetting maxEntityCollisionsPerTick = new IntSetting(this, "perfomance.maxEntityCollisionsPerTick",8,"Max Entity Collisions PerTick");

    public final StringSetting server_type = new StringSetting(this, "server-type", "FML", "Set the server type displayed in motd (FML/BUKKIT/VANILLA)");
    public final StringSetting lang = new StringSetting(this, "lang", "en_US", "Mohist internationalization language setting, will return the default system language when your settings are invalid");
    public final StringSetting console_name = new StringSetting(this, "console_name", "Server", "Front of the console, for example /say");
    public final BoolSetting support_nocmd = new BoolSetting(this, "support_nocmd", false, "Some server tools do not recognize I18N");
    public final BoolSetting watchmohist = new BoolSetting(this, "watch_mohist", true, "mohist watch");

    // Bukkit Event Canceled
    public final BoolSetting explosion_canceled = new BoolSetting(this, "eventCanceled.explosion", false, "BlockExplosionEvent isCanceled");
    public final BoolSetting check_update = new BoolSetting(this, "check_update", true, "Check Update");

    public final BoolSetting asyn_aimove = new BoolSetting(this, "asyn.aimove", false, "Ai-Move");
    public final BoolSetting asyn_spawnerCreature = new BoolSetting(this, "asyn.spawnerCreature", false, "spawnerCreature");
    public final BoolSetting asyn_checkLight = new BoolSetting(this, "asyn.checkLight", false, "checkLight");

    public final StringSetting ANSI_ERROR_LEVEL = new StringSetting(this, "consolecolor.error-level", "[31;1m", "consolecolor.error-level");
    public final StringSetting ANSI_WARN_LEVEL = new StringSetting(this, "consolecolor.warnlevel", "[33;1m", "consolecolor.warn-level");
    public final StringSetting ANSI_ERROR_MSG = new StringSetting(this, "consolecolor.error-msg", "[31;1m", "consolecolor.error-msg");
    public final StringSetting ANSI_WARN_MSG = new StringSetting(this, "consolecolor.warn-msg", "[33;1m", "consolecolor.warn-msg");


    /* ======================================================================== */

    public MohistConfig()
    {
        super("mohist.yml", "mohist");
        init();
        instance = this;
    }

    public void init()
    {
        for(Field f : this.getClass().getFields())
        {
            if(Modifier.isFinal(f.getModifiers()) && Modifier.isPublic(f.getModifiers()) && !Modifier.isStatic(f.getModifiers()))
            {
                try
                {
                    Setting setting = (Setting) f.get(this);
                    if(setting == null) {
                        continue;
                    }
                    settings.put(setting.path, setting);
                }
                catch (ClassCastException e)
                {

                }
                catch(Throwable t)
                {
                    System.out.println("[Mohist] Failed to initialize a MohistConfig setting.");
                    t.printStackTrace();
                }
            }
        }
        load();
    }

    @Override
    public void addCommands()
    {
        commands.put("mohist", new MohistCommand("mohist"));
        commands.put("item", new ItemCommand("item"));
        commands.put("version", new VersionCommand("version"));
        commands.put("Dump", new DumpCommand("version"));
    }

    @Override
    public void load()
    {
        try
        {
            config = YamlConfiguration.loadConfiguration(configFile);
            String header = HEADER + "\n";
            for (Setting toggle : settings.values())
            {
                if (!toggle.description.equals("")) {
                    header += "Setting: " + toggle.path + " Default: " + toggle.def + "   # " + toggle.description + "\n";
                }

                config.addDefault(toggle.path, toggle.def);
                settings.get(toggle.path).setValue(config.getString(toggle.path));
            }
            config.options().header(header);
            config.options().copyDefaults(true);

            version = getInt("config-version", 1);
            set("config-version", 1);

            this.save();
        }
        catch (Exception ex)
        {
            ServerAPI.getNMSServer().logSevere("Could not load " + this.configFile);
            ex.printStackTrace();
        }
    }
}
