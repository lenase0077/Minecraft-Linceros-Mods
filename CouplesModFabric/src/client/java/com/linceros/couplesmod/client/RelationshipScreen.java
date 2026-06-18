package com.linceros.couplesmod.client;

import com.linceros.couplesmod.attachment.RelationshipData;
import com.linceros.couplesmod.network.SendGiftPacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import com.linceros.couplesmod.CouplesMod;

public class RelationshipScreen extends Screen {

    private final boolean showGiftMenu;
    private static final Identifier MENU_TEXTURE = Identifier.fromNamespaceAndPath(CouplesMod.MODID, "textures/gui/couples_menu.png");
    private static final Identifier GIFT_BUTTON_TEXTURE = Identifier.fromNamespaceAndPath(CouplesMod.MODID, "textures/gui/boton_regalo.png");
    private static final Identifier GIFT_BUTTON_HIGHLIGHT = Identifier.fromNamespaceAndPath(CouplesMod.MODID, "textures/gui/boton_regalo_highlight.png");
    private final int imageWidth = 176;
    private final int imageHeight = 166;

    public RelationshipScreen(boolean showGiftMenu) {
        super(Component.literal(showGiftMenu ? "Send Gift" : "Relationship Status"));
        this.showGiftMenu = showGiftMenu;
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void renderBackground(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        // NO HACER NADA - Desactiva cualquier blur nativo
    }

    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        if (this.showGiftMenu && RelationshipDataCache.status != RelationshipData.Status.NONE) {
            int buttonSize = 32;
            this.addRenderableWidget(new CustomImageButton(centerX - buttonSize / 2, centerY + 20, buttonSize, buttonSize, GIFT_BUTTON_TEXTURE, GIFT_BUTTON_HIGHLIGHT, button -> {
                ClientPlayNetworking.send(new SendGiftPacket());
                this.onClose();
            }));
        }
    }

    public void extractRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        // No renderBackground call at all!

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        // Blit explicitly using 176x166 texture size to enforce 1:1 mapping
        GuiGraphicsExtractor.blit(MENU_TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 176, 166);
        
        // Draw widgets on top of background
        super.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);

        if (RelationshipDataCache.status == RelationshipData.Status.NONE) {
            GuiGraphicsExtractor.centeredText(this.font, "§7You are currently single.", centerX, centerY - 10, 0xFFFFFF);
            
            GuiGraphicsExtractor.pose().pushMatrix();
            GuiGraphicsExtractor.pose().scale(0.8f, 0.8f);
            GuiGraphicsExtractor.centeredText(this.font, "Craft a bouquet and right-click", (int)(centerX / 0.8f), (int)((centerY + 15) / 0.8f), 0xAAAAAA);
            GuiGraphicsExtractor.centeredText(this.font, "someone to start dating!", (int)(centerX / 0.8f), (int)((centerY + 30) / 0.8f), 0xAAAAAA);
            GuiGraphicsExtractor.pose().popMatrix();
        } else {
            String statusText = RelationshipDataCache.status == RelationshipData.Status.MARRIED ? "§b✨ Married ✨" : "§d💕 Dating 💕";
            GuiGraphicsExtractor.centeredText(this.font, statusText, centerX, centerY - 45, 0xFFFFFF);
            
            String partnerName = "Offline Partner";
            if (this.minecraft != null && this.minecraft.level != null && RelationshipDataCache.partnerUuid != null) {
                net.minecraft.world.entity.player.Player partner = this.minecraft.level.getPlayerByUUID(RelationshipDataCache.partnerUuid);
                if (partner != null) {
                    partnerName = partner.getName().getString();
                }
            }
            GuiGraphicsExtractor.centeredText(this.font, "Partner: §e" + partnerName, centerX, centerY - 25, 0xFFFFFF);
            
            int currentLevel = RelationshipDataCache.level;
            int currentXp = RelationshipDataCache.xp;
            int requiredXp = currentLevel * 100;
            String levelText = "§eLevel " + currentLevel + " §8(§7" + currentXp + " / " + requiredXp + " XP§8)";
            GuiGraphicsExtractor.centeredText(this.font, levelText, centerX, centerY - 5, 0xFFFFFF);
            
            if (this.showGiftMenu) {
                GuiGraphicsExtractor.pose().pushMatrix();
                GuiGraphicsExtractor.pose().scale(0.8f, 0.8f);
                GuiGraphicsExtractor.centeredText(this.font, "Hold an item and click 'Send Gift'", (int)(centerX / 0.8f), (int)((centerY + 50) / 0.8f), 0xAAAAAA);
                GuiGraphicsExtractor.centeredText(this.font, "to give it to your partner!", (int)(centerX / 0.8f), (int)((centerY + 60) / 0.8f), 0xAAAAAA);
                GuiGraphicsExtractor.pose().popMatrix();
            }
        }
    }

    private static class CustomImageButton extends Button {
        private final Identifier texture;
        private final Identifier hoverTexture;

        public CustomImageButton(int x, int y, int width, int height, Identifier texture, Identifier hoverTexture, OnPress onPress) {
            super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
            this.texture = texture;
            this.hoverTexture = hoverTexture;
        }

        public void extractContents(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
            Identifier currentTexture = this.isHovered() ? this.hoverTexture : this.texture;
            GuiGraphicsExtractor.blit(currentTexture, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
        }
    }
}
