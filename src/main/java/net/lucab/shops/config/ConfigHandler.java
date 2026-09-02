package net.lucab.shops.config;

import com.electronwill.nightconfig.core.file.FileConfig;

import net.lucab.shops.screen.ToggleButtonID;

import static net.lucab.shops.SpudaciousShops.MOD_ID;

import java.io.File;
import java.util.EnumMap;

public class ConfigHandler {
    private static final File CONFIG_PATH = new File("config/"+MOD_ID+".toml");
    public static FileConfig config;


    private static final EnumMap<ToggleButtonID, Boolean> toggleSettingsStates = getDefaultToggleSettingStates();


    private static EnumMap<ToggleButtonID, Boolean> getDefaultToggleSettingStates() {
        EnumMap<ToggleButtonID, Boolean> tss = new EnumMap<>(ToggleButtonID.class);
        config = FileConfig.of(CONFIG_PATH);
        config.load();

        String TogEffectID = "ToggleEffectsDefault";
        boolean effectsEnabled = true;
        if (!config.contains(TogEffectID)) {
            config.set(TogEffectID, true);
            config.save();
        } else {
            effectsEnabled = config.getOrElse(TogEffectID, true);
        }

        //ugly but ensures all values are covered
        for (ToggleButtonID value : ToggleButtonID.values()) {
            tss.put(value,
                    switch (value){
                        case CreativeToggle -> false;
                        case ShopStyleToggle -> false;
                        case IgnoreNBTToggle -> false;
                        case EffectsToggle -> effectsEnabled;
                    }
                    );
        }
        return tss;
    }
    public static Boolean getDefaultToggleSetting(ToggleButtonID ID) {
        return toggleSettingsStates.getOrDefault(ID,false);
    }
    public static void initialise(){}
}
