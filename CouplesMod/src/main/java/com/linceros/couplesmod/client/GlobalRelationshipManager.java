package com.linceros.couplesmod.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linceros.couplesmod.attachment.RelationshipData;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Optional;
import java.util.UUID;

public class GlobalRelationshipManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static File getConfigFile() {
        return new File(Minecraft.getInstance().gameDirectory, "config/couplesmod_global.json");
    }

    public static RelationshipData loadGlobalData() {
        File file = getConfigFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                GlobalData data = GSON.fromJson(reader, GlobalData.class);
                if (data != null && data.partnerUuid != null) {
                    return new RelationshipData(
                            RelationshipData.Status.valueOf(data.status),
                            Optional.of(UUID.fromString(data.partnerUuid)),
                            data.level,
                            data.xp,
                            data.lastGiftTime,
                            data.lastKissTime,
                            data.startDate,
                            data.kisses,
                            data.gifts,
                            data.sharedBeds
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return RelationshipData.EMPTY;
    }

    public static void saveGlobalData(RelationshipData data) {
        File file = getConfigFile();
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            GlobalData gd = new GlobalData();
            gd.status = data.status().name();
            gd.partnerUuid = data.partnerUuid().map(UUID::toString).orElse(null);
            gd.level = data.level();
            gd.xp = data.xp();
            gd.lastGiftTime = data.lastGiftTime();
            gd.lastKissTime = data.lastKissTime();
            gd.startDate = data.startDate();
            gd.kisses = data.kisses();
            gd.gifts = data.gifts();
            gd.sharedBeds = data.sharedBeds();
            GSON.toJson(gd, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class GlobalData {
        public String status = "NONE";
        public String partnerUuid = null;
        public int level = 1;
        public int xp = 0;
        public long lastGiftTime = 0;
        public long lastKissTime = 0;
        public long startDate = 0;
        public int kisses = 0;
        public int gifts = 0;
        public int sharedBeds = 0;
    }
}
