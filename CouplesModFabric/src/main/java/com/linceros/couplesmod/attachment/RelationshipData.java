package com.linceros.couplesmod.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import java.util.Optional;
import java.util.UUID;

public record RelationshipData(Status status, Optional<UUID> partnerUuid, int level, int xp, long lastGiftTime, long lastKissTime, long startDate, int kisses, int gifts, int sharedBeds) {
    public enum Status {
        NONE,
        DATING,
        MARRIED
    }

    public static final Codec<Status> STATUS_CODEC = Codec.STRING.xmap(Status::valueOf, Status::name);

    public static final Codec<RelationshipData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            STATUS_CODEC.fieldOf("status").forGetter(RelationshipData::status),
            UUIDUtil.CODEC.optionalFieldOf("partner_uuid").forGetter(RelationshipData::partnerUuid),
            Codec.INT.optionalFieldOf("level", 1).forGetter(RelationshipData::level),
            Codec.INT.optionalFieldOf("xp", 0).forGetter(RelationshipData::xp),
            Codec.LONG.optionalFieldOf("last_gift_time", 0L).forGetter(RelationshipData::lastGiftTime),
            Codec.LONG.optionalFieldOf("last_kiss_time", 0L).forGetter(RelationshipData::lastKissTime),
            Codec.LONG.optionalFieldOf("start_date", 0L).forGetter(RelationshipData::startDate),
            Codec.INT.optionalFieldOf("kisses", 0).forGetter(RelationshipData::kisses),
            Codec.INT.optionalFieldOf("gifts", 0).forGetter(RelationshipData::gifts),
            Codec.INT.optionalFieldOf("shared_beds", 0).forGetter(RelationshipData::sharedBeds)
    ).apply(instance, RelationshipData::new));

    public static final RelationshipData EMPTY = new RelationshipData(Status.NONE, Optional.empty(), 1, 0, 0L, 0L, 0L, 0, 0, 0);

    // Helper methods to make it easier to copy and update
    public RelationshipData withStatus(Status newStatus) {
        return new RelationshipData(newStatus, this.partnerUuid, this.level, this.xp, this.lastGiftTime, this.lastKissTime, this.startDate, this.kisses, this.gifts, this.sharedBeds);
    }

    public RelationshipData withPartner(UUID newPartner) {
        return new RelationshipData(this.status, Optional.of(newPartner), this.level, this.xp, this.lastGiftTime, this.lastKissTime, this.startDate, this.kisses, this.gifts, this.sharedBeds);
    }

    public RelationshipData withXp(int newLevel, int newXp) {
        return new RelationshipData(this.status, this.partnerUuid, newLevel, newXp, this.lastGiftTime, this.lastKissTime, this.startDate, this.kisses, this.gifts, this.sharedBeds);
    }

    public RelationshipData withGiftTime(long time) {
        return new RelationshipData(this.status, this.partnerUuid, this.level, this.xp, time, this.lastKissTime, this.startDate, this.kisses, this.gifts, this.sharedBeds);
    }

    public RelationshipData withKissTime(long time) {
        return new RelationshipData(this.status, this.partnerUuid, this.level, this.xp, this.lastGiftTime, time, this.startDate, this.kisses, this.gifts, this.sharedBeds);
    }
    
    public RelationshipData withStartDate(long time) {
        return new RelationshipData(this.status, this.partnerUuid, this.level, this.xp, this.lastGiftTime, this.lastKissTime, time, this.kisses, this.gifts, this.sharedBeds);
    }
    
    public RelationshipData withKisses(int k) {
        return new RelationshipData(this.status, this.partnerUuid, this.level, this.xp, this.lastGiftTime, this.lastKissTime, this.startDate, k, this.gifts, this.sharedBeds);
    }
    
    public RelationshipData withGifts(int g) {
        return new RelationshipData(this.status, this.partnerUuid, this.level, this.xp, this.lastGiftTime, this.lastKissTime, this.startDate, this.kisses, g, this.sharedBeds);
    }
    
    public RelationshipData withSharedBeds(int b) {
        return new RelationshipData(this.status, this.partnerUuid, this.level, this.xp, this.lastGiftTime, this.lastKissTime, this.startDate, this.kisses, this.gifts, b);
    }

    public RelationshipData clearPartner() {
        return new RelationshipData(Status.NONE, Optional.empty(), 1, 0, 0L, 0L, 0L, 0, 0, 0);
    }
}
