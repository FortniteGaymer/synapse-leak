package com.ragemachine.moon.impl.client.modules.render;

import com.ragemachine.moon.MoonHack;
import com.ragemachine.moon.api.event.events.Render3DEvent;
import com.ragemachine.moon.impl.client.modules.Module;
import com.ragemachine.moon.impl.client.setting.Value;
import com.ragemachine.moon.impl.util.DamageUtil;
import com.ragemachine.moon.impl.util.EntityUtil;
import com.ragemachine.moon.impl.util.RenderUtil;
import com.ragemachine.moon.impl.util.TextUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

public class Nametags extends Module {

    private final Value<Boolean> health = addSetting(new Value("Health", true));
    private final Value<Boolean> armor = addSetting(new Value("Armor", true));
    private final Value<Float> scaling = addSetting(new Value("Size", 0.3f, 0.1f, 20.0f));
    private final Value<Boolean> ping = addSetting(new Value("Ping", true));
    final Value<Boolean> rect = addSetting(new Value("Rectangle", true));
    final Value<Boolean> outline = addSetting(new Value("Outline", true));
    public Value<Integer> red = addSetting(new Value("Red", 255, 0, 255, v -> rect.getValue()));
    public Value<Integer> green = addSetting(new Value("Green", 255, 0, 255, v -> rect.getValue()));
    public Value<Integer> blue = addSetting(new Value("Blue", 255, 0, 255, v -> rect.getValue()));
    public Value<Integer> alpha = addSetting(new Value("Alpha", 255, 0, 255, v -> rect.getValue()));
    public Value<Integer> oRed = addSetting(new Value("Outline Red", 255, 0, 255, v -> outline.getValue()));
    public Value<Integer> oGreen = addSetting(new Value("Outline Green", 255, 0, 255, v -> outline.getValue()));
    public Value<Integer> oBlue = addSetting(new Value("Outline Blue", 255, 0, 255, v -> outline.getValue()));
    public Value<Integer> oAlpha = addSetting(new Value("Outline Alpha", 255, 0, 255, v -> rect.getValue()));
    public Value<Integer> width = addSetting(new Value("Outline Width", 1, 0.1, 2, v -> outline.getValue()));



    private static Nametags INSTANCE = new Nametags();

    public Nametags() {
        super("Nametags", "Better Nametags", Category.RENDER, false, false, false);
        setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static Nametags getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Nametags();
        }
        return INSTANCE;
    }

    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        double tempY = y;
        tempY += (player.isSneaking() ? 0.5D : 0.7D);
        Entity camera = mc.getRenderViewEntity();
        assert camera != null;
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);

        String displayTag = getDisplayTag(player);
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = renderer.getStringWidth(displayTag) / 2;
        double scale = (0.0018 + scaling.getValue() * (distance * 0.3)) / 1000.0;


        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1, -1500000);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4F, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();

        int color = new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue()).getRGB();
        int oColor = new Color(oRed.getValue(), oGreen.getValue(), oBlue.getValue(), oAlpha.getValue()).getRGB();
        if(rect.getValue()) {
            RenderUtil.drawRect(-width - 2, -(renderer.getFontHeight() + 1), width + 2F, 1.5F,color);
        }

        if (outline.getValue()) {
            RenderUtil.drawBorderedRect(-width - 2, -(renderer.getFontHeight() + 1), width + 2, 1.5, 1, new Color(0, 0, 0, 0).getRGB(), oColor);
        }
        ItemStack renderMainHand = player.getHeldItemMainhand().copy();
        if(renderMainHand.hasEffect() && (renderMainHand.getItem() instanceof ItemTool || renderMainHand.getItem() instanceof ItemArmor)) {
            renderMainHand.stackSize = 1;
        }

        String stackName = renderMainHand.getDisplayName();
        int stackNameWidth = renderer.getStringWidth(stackName) / 2;
        GL11.glPushMatrix();
        GL11.glScalef(0.75f, 0.75f, 0);
        renderer.drawStringWithShadow(stackName, -stackNameWidth, -(getBiggestArmorTag(player) + 20), 0xFFFFFFFF);
        GL11.glScalef(1.5f, 1.5f, 1);
        GL11.glPopMatrix();

        if(armor.getValue()) {
            GlStateManager.pushMatrix();
            int xOffset = -8;
            for (ItemStack stack : player.inventory.armorInventory) {
                if (stack != null) {
                    xOffset -= 8;
                }
            }

            xOffset -= 8;
            ItemStack renderOffhand = player.getHeldItemOffhand().copy();
            if(renderOffhand.hasEffect() && (renderOffhand.getItem() instanceof ItemTool || renderOffhand.getItem() instanceof ItemArmor)) {
                renderOffhand.stackSize = 1;
            }

            this.renderItemStack(renderOffhand, xOffset, -26);
            xOffset += 16;

            for (ItemStack stack : player.inventory.armorInventory) {
                if (stack != null) {
                    ItemStack armourStack = stack.copy();
                    if (armourStack.hasEffect() && (armourStack.getItem() instanceof ItemTool || armourStack.getItem() instanceof ItemArmor)) {
                        armourStack.stackSize = 1;
                    }

                    this.renderItemStack(armourStack, xOffset, -26);
                    xOffset += 16;
                }
            }

            this.renderItemStack(renderMainHand, xOffset, -26);

            GlStateManager.popMatrix();
        }

        renderer.drawStringWithShadow(displayTag, -width, -(renderer.getFontHeight() - 1), this.getDisplayColour(player));

        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1, 1500000);
        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(GL11.GL_ACCUM);

        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0F;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();

        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, y);

        mc.getRenderItem().zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();

        GlStateManager.enableCull();
        GlStateManager.enableAlpha();

        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.disableDepth();
        renderEnchantmentText(stack, x, y);
        GlStateManager.enableDepth();
        GlStateManager.scale(2F, 2F, 2F);
        GlStateManager.popMatrix();
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        for(EntityPlayer player : mc.world.playerEntities) {
            if(player != null && !player.equals(mc.player) && player.isEntityAlive()) {
                double x = interpolate(player.lastTickPosX, player.posX, event.getPartialTicks()) - mc.getRenderManager().renderPosX;
                double y = interpolate(player.lastTickPosY, player.posY, event.getPartialTicks()) - mc.getRenderManager().renderPosY;
                double z = interpolate(player.lastTickPosZ, player.posZ, event.getPartialTicks()) - mc.getRenderManager().renderPosZ;
                renderNameTag(player, x, y, z, event.getPartialTicks());
            }
        }
    }


    private void renderEnchantmentText(ItemStack stack, int x, int y) {
        int enchantmentY = y - 8;

        if(stack.getItem() == Items.GOLDEN_APPLE && stack.hasEffect()) {
            renderer.drawStringWithShadow("god", x * 2, enchantmentY, 0xFFc34d41);
            enchantmentY -= 8;
        }

        NBTTagList enchants = stack.getEnchantmentTagList();
        for(int index = 0; index < enchants.tagCount(); ++index) {
            short id = enchants.getCompoundTagAt(index).getShort("id");
            short level = enchants.getCompoundTagAt(index).getShort("lvl");
            Enchantment enc = Enchantment.getEnchantmentByID(id);
            if (enc != null) {
                String encName = enc.isCurse()
                        ? TextFormatting.RED
                        + enc.getTranslatedName(level).substring(11).substring(0, 1).toLowerCase()
                        : enc.getTranslatedName(level).substring(0, 1).toLowerCase();
                encName = encName + level;
                renderer.drawStringWithShadow(encName, x * 2, enchantmentY, -1);
                enchantmentY -= 8;
            }
        }
        if(DamageUtil.hasDurability(stack)) {
            int percent = DamageUtil.getRoundedDamage(stack);
            String color;
            if(percent >= 60) {
                color = TextUtil.GREEN;
            } else if(percent >= 25) {
                color = TextUtil.YELLOW;
            } else {
                color = TextUtil.RED;
            }
            renderer.drawStringWithShadow(color + percent + "%", x * 2, enchantmentY, 0xFFFFFFFF);
        }
    }

    private float getBiggestArmorTag(EntityPlayer player) {
        float enchantmentY = 0;
        boolean arm = false;
        for (ItemStack stack : player.inventory.armorInventory) {
            float encY = 0;
            if (stack != null) {
                NBTTagList enchants = stack.getEnchantmentTagList();
                for (int index = 0; index < enchants.tagCount(); ++index) {
                    short id = enchants.getCompoundTagAt(index).getShort("id");
                    Enchantment enc = Enchantment.getEnchantmentByID(id);
                    if (enc != null) {
                        encY += 8;
                        arm = true;
                    }
                }
            }
            if (encY > enchantmentY) enchantmentY = encY;
        }
        ItemStack renderMainHand = player.getHeldItemMainhand().copy();
        if(renderMainHand.hasEffect()) {
            float encY = 0;
            NBTTagList enchants = renderMainHand.getEnchantmentTagList();
            for (int index = 0; index < enchants.tagCount(); ++index) {
                short id = enchants.getCompoundTagAt(index).getShort("id");
                Enchantment enc = Enchantment.getEnchantmentByID(id);
                if (enc != null) {
                    encY += 8;
                    arm = true;
                }
            }
            if (encY > enchantmentY) enchantmentY = encY;
        }
        ItemStack renderOffHand = player.getHeldItemOffhand().copy();
        if(renderOffHand.hasEffect()) {
            float encY = 0;
            NBTTagList enchants = renderOffHand.getEnchantmentTagList();
            for (int index = 0; index < enchants.tagCount(); ++index) {
                short id = enchants.getCompoundTagAt(index).getShort("id");
                Enchantment enc = Enchantment.getEnchantmentByID(id);
                if (enc != null) {
                    encY += 8;
                    arm = true;
                }
            }
            if (encY > enchantmentY) enchantmentY = encY;
        }
        return (arm ? 0 : 20) + enchantmentY;
    }

    private String getDisplayTag(EntityPlayer player) {
        String name = player.getDisplayName().getFormattedText();
        if(name.contains(mc.getSession().getUsername())) {
            name = "You";
        }

        if (!health.getValue()) {
            return name;
        }

        float health = EntityUtil.getHealth(player);
        String color;

        if (health > 18) {
            color = TextUtil.GREEN;
        } else if (health > 16) {
            color = TextUtil.DARK_GREEN;
        } else if (health > 12) {
            color = TextUtil.YELLOW;
        } else if (health > 8) {
            color = TextUtil.GOLD;
        } else if (health > 5) {
            color = TextUtil.RED;
        } else {
            color = TextUtil.DARK_RED;
        }

        String pingStr = "";
        if(ping.getValue()) {
            try {
                final int responseTime = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime();
                pingStr += responseTime + "ms ";
            } catch (Exception ignored) {}
        }

        return pingStr + name;
    }

    private int getDisplayColour(EntityPlayer player) {
        int colour = 0xFFAAAAAA;
        if(Nametags.getInstance().isEnabled()) {
            colour = 0xFFFFFFFF;
        }
        if(MoonHack.getFriendManager.isFriend(player)) {
            return new Color(0, 255, 255).getRGB();
        } else if (player.isInvisible()) {
            colour = new Color(125, 125, 125).getRGB();
        } else if (player.isSneaking()) {
            colour = new Color(255, 255, 0).getRGB();
        }
        return colour;
    }

    private double interpolate(double previous, double current, float delta) {
        return (previous + (current - previous) * delta);
    }
}