package com.linceros.couplesmod.client;

import com.linceros.couplesmod.attachment.RelationshipData;
import java.util.UUID;

public class RelationshipDataCache {
    public static RelationshipData.Status status = RelationshipData.Status.NONE;
    public static UUID partnerUuid = null;
    public static int level = 1;
    public static int xp = 0;
    public static long lastGiftTime = 0;
    public static long lastKissTime = 0;
    public static long startDate = 0;
    public static int kisses = 0;
    public static int gifts = 0;
    public static int sharedBeds = 0;
}
