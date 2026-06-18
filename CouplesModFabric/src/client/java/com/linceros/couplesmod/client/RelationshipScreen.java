package com.linceros.couplesmod.client;

import com.linceros.couplesmod.attachment.RelationshipData;
import com.linceros.couplesmod.network.SendGiftPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import com.linceros.couplesmod.CouplesMod;

public class RelationshipScreen extends Screen {

    private final boolean showGiftMenu;
    private static final ResourceLocation MENU_TEXTURE = ResourceLocation.fromNamespaceAndPath(CouplesMod.MODID, "textures/gui/couples_menu.png");
    private static final ResourceLocation GIFT_BUTTON_TEXTURE = ResourceLocation.fromNamespaceAndPath(CouplesMod.MODID, "textures/gui/boton_regalo.png");
    private static final ResourceLocation GIFT_BUTTON_HIGHLIGHT = ResourceLocation.fromNamespaceAndPath(CouplesMod.MODID, "textures/gui/boton_regalo_highlight.png");
    private final int imageWidth = 176;
    private final int imageHeight = 166;

    public RelationshipScreen(boolean showGiftMenu) {
        super(Component.literal(showGiftMenu ? "Send Gift" : "Relationship Status"));
        this.showGiftMenu = showGiftMenu;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // NO HACER NADA - Desactiva cualquier blur nativo
    }

    @Override
    protected void init() {
        super.init();
        
        if (this.minecraft != null && this.minecraft.gameRenderer != null) {
            this.minecraft.gameRenderer.shutdownEffect();
        }
        
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

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // No renderBackground call at all!

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        
        int leftPos = (this.width - this.imageWidth) / 2;
        int topPos = (this.height - this.imageHeight) / 2;

        // Blit explicitly using 176x166 texture size to enforce 1:1 mapping
        guiGraphics.blit(MENU_TEXTURE, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 176, 166);
        
        // Draw widgets on top of background
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (RelationshipDataCache.status == RelationshipData.Status.NONE) {
            guiGraphics.drawCenteredString(this.font, "§7You are currently single.", centerX, centerY - 10, 0xFFFFFF);
            
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.8f, 0.8f, 0.8f);
            guiGraphics.drawCenteredString(this.font, "Craft a bouquet and right-click", (int)(centerX / 0.8f), (int)((centerY + 15) / 0.8f), 0xAAAAAA);
            guiGraphics.drawCenteredString(this.font, "someone to start dating!", (int)(centerX / 0.8f), (int)((centerY + 30) / 0.8f), 0xAAAAAA);
            guiGraphics.pose().popPose();
        } else {
            String statusText = RelationshipDataCache.status == RelationshipData.Status.MARRIED ? "§b✨ Married ✨" : "§d💕 Dating 💕";
            guiGraphics.drawCenteredString(this.font, statusText, centerX, centerY - 45, 0xFFFFFF);
            
            String partnerName = "Offline Partner";
            if (this.minecraft != null && this.minecraft.level != null && RelationshipDataCache.partnerUuid != null) {
                net.minecraft.world.entity.player.Player partner = this.minecraft.level.getPlayerByUUID(RelationshipDataCache.partnerUuid);
                if (partner != null) {
                    partnerName = partner.getName().getString();
                }
            }
            guiGraphics.drawCenteredString(this.font, "Partner: §e" + partnerName, centerX, centerY - 25, 0xFFFFFF);
            
            int currentLevel = RelationshipDataCache.level;
            int currentXp = RelationshipDataCache.xp;
            int requiredXp = currentLevel * 100;
            String levelText = "§eLevel " + currentLevel + " §8(§7" + currentXp + " / " + requiredXp + " XP§8)";
            guiGraphics.drawCenteredString(this.font, levelText, centerX, centerY - 5, 0xFFFFFF);
            
            if (this.showGiftMenu) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().scale(0.8f, 0.8f, 0.8f);
                guiGraphics.drawCenteredString(this.font, "Hold an item and click 'Send Gift'", (int)(centerX / 0.8f), (int)((centerY + 50) / 0.8f), 0xAAAAAA);
                guiGraphics.drawCenteredString(this.font, "to give it to your partner!", (int)(centerX / 0.8f), (int)((centerY + 60) / 0.8f), 0xAAAAAA);
                guiGraphics.pose().popPose();
            }
        }
    }

    private static class CustomImageButton extends Button {
        private final ResourceLocation texture;
        private final ResourceLocation hoverTexture;

        public CustomImageButton(int x, int y, int width, int height, ResourceLocation texture, ResourceLocation hoverTexture, OnPress onPress) {
            super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
            this.texture = texture;
            this.hoverTexture = hoverTexture;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            ResourceLocation currentTexture = this.isHovered() ? this.hoverTexture : this.texture;
            guiGraphics.blit(currentTexture, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
        }
    }
}
