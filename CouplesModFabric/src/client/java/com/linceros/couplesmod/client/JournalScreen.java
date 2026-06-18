package com.linceros.couplesmod.client;

import com.linceros.couplesmod.attachment.RelationshipData;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class JournalScreen extends Screen {
    private final RelationshipData data;
    // We can just use the vanilla book GUI texture
    private static final Identifier BOOK_TEXTURE = Identifier.withDefaultNamespace("textures/gui/book.png");
    private final int bookWidth = 192;
    private final int bookHeight = 192;

    public JournalScreen(RelationshipData data) {
        super(Component.literal("Anniversary Journal"));
        this.data = data;
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void extractRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);

        int startX = (this.width - this.bookWidth) / 2;
        int startY = (this.height - this.bookHeight) / 2;

        // Draw the book background
        GuiGraphicsExtractor.blit(BOOK_TEXTURE, startX, startY, this.bookWidth, this.bookHeight, 0.0f, 0.0f, (float)this.bookWidth, (float)this.bookHeight);

        String partnerName = "Your Partner";
        if (this.minecraft != null && this.minecraft.level != null && data.partnerUuid().isPresent()) {
            Player partner = this.minecraft.level.getPlayerByUUID(data.partnerUuid().get());
            if (partner != null) {
                partnerName = partner.getName().getString();
            }
        }

        // Calculate days together
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - data.startDate();
        long daysTogether = diff / (1000L * 60 * 60 * 24);
        if (data.startDate() == 0) daysTogether = 0; // fallback if they just updated the mod

        GuiGraphicsExtractor.text(this.font, "§d❤ Anniversary Journal ❤", startX + (this.bookWidth - this.font.width("❤ Anniversary Journal ❤")) / 2, startY + 20, 0xFF000000, false);
        
        GuiGraphicsExtractor.text(this.font, "Partner: §5" + partnerName, startX + 36, startY + 45, 0xFF000000, false);
        GuiGraphicsExtractor.text(this.font, "Level: §5" + data.level(), startX + 36, startY + 60, 0xFF000000, false);
        
        GuiGraphicsExtractor.text(this.font, "Days Together: §5" + daysTogether, startX + 36, startY + 85, 0xFF000000, false);
        GuiGraphicsExtractor.text(this.font, "Kisses Given: §5" + data.kisses(), startX + 36, startY + 100, 0xFF000000, false);
        GuiGraphicsExtractor.text(this.font, "Gifts Exchanged: §5" + data.gifts(), startX + 36, startY + 115, 0xFF000000, false);
        GuiGraphicsExtractor.text(this.font, "Times Slept Together: §5" + data.sharedBeds(), startX + 36, startY + 130, 0xFF000000, false);
        
        GuiGraphicsExtractor.text(this.font, "§8True love lasts forever.", startX + (this.bookWidth - this.font.width("True love lasts forever.")) / 2, startY + 160, 0xFF000000, false);
    }
}
