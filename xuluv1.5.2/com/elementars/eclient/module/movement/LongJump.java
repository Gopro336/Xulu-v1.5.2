// 
// Decompiled by Procyon v0.5.36
// 

package com.elementars.eclient.module.movement;

import java.util.Arrays;
import java.util.Base64;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import net.minecraft.potion.PotionEffect;
import com.elementars.eclient.event.EventTarget;
import net.minecraft.init.MobEffects;
import com.elementars.eclient.event.Event;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.elementars.eclient.module.Category;
import dev.xulu.settings.Value;
import me.zero.alpine.listener.EventHandler;
import com.elementars.eclient.event.events.PlayerMoveEvent;
import me.zero.alpine.listener.Listener;
import com.elementars.eclient.module.Module;

public class LongJump extends Module
{
    private static String[] llIlIIl;
    private static int[] llIllII;
    private double motionSpeed;
    private int currentState;
    @EventHandler
    private Listener<PlayerMoveEvent> packetEventListener;
    private double prevDist;
    Value<Double> multiplier;
    Value<Boolean> autoSprint;
    Value<Boolean> accelerationTimer;
    Value<Integer> timerSpeed;
    Value<Boolean> speedDetect;
    Value<Boolean> jumpDetect;
    Value<Double> extraYBoost;
    Value<Boolean> chat;
    private boolean attempting;
    
    public LongJump() {
        super("LongJump", "hop around", 0, Category.MOVEMENT, true);
        this.multiplier = this.register(new Value<Double>("Multiplier", this, 4.1, 1.0, 10.0));
        this.autoSprint = this.register(new Value<Boolean>("Auto Sprint", this, false));
        this.accelerationTimer = this.register(new Value<Boolean>("Acceleration Timer", this, false));
        this.timerSpeed = this.register(new Value<Integer>("Timer Speed", this, 1, 0, 10));
        this.speedDetect = this.register(new Value<Boolean>("Speed Detect", this, true));
        this.jumpDetect = this.register(new Value<Boolean>("Leaping Detect", this, true));
        this.extraYBoost = this.register(new Value<Double>("Extra Y Boost", this, 0.0, 0.0, 1.0));
        this.chat = this.register(new Value<Boolean>("Toggle msgs", this, false));
        this.attempting = false;
    }
    
    @Override
    public void onEnable() {
        if (this.chat.getValue()) {
            this.sendDebugMessage(ChatFormatting.GREEN + "Enabled!");
        }
        this.attempting = false;
    }
    
    @Override
    public void onDisable() {
        if (this.chat.getValue()) {
            this.sendDebugMessage(ChatFormatting.RED + "Disabled!");
        }
    }
    
    @Override
    public String getHudInfo() {
        return "Speed";
    }
    
    private static void lIIlIIIII() {
        (LongJump.llIllII = new int[10])[0] = 0;
        LongJump.llIllII[1] = " ".length();
        LongJump.llIllII[2] = "  ".length();
        LongJump.llIllII[3] = "   ".length();
        LongJump.llIllII[4] = 10;
        LongJump.llIllII[5] = 4;
        LongJump.llIllII[6] = 5;
        LongJump.llIllII[7] = 6;
        LongJump.llIllII[8] = 7;
        LongJump.llIllII[9] = 8;
    }
    
    private static int lIIlIIIIl(final float var0, final float var1) {
        final float var2;
        return ((var2 = var0 - var1) == 0.0f) ? 0 : ((var2 < 0.0f) ? -1 : 1);
    }
    
    private static boolean lIIlIlIII(final int lllIIlIIlIIIlll) {
        return lllIIlIIlIIIlll <= 0;
    }
    
    private static int lIIlIIllI(final float var0, final float var1) {
        final float var2;
        return ((var2 = var0 - var1) == 0.0f) ? 0 : ((var2 < 0.0f) ? -1 : 1);
    }
    
    private static boolean lIIlIlIIl(final int lllIIlIIlIIIlIl) {
        return lllIIlIIlIIIlIl > 0;
    }
    
    @Override
    public void onUpdate() {
        if (isNull(LongJump.mc.player)) {
            return;
        }
        this.prevDist = Math.sqrt((LongJump.mc.player.posX - LongJump.mc.player.prevPosX) * (LongJump.mc.player.posX - LongJump.mc.player.prevPosX) + (LongJump.mc.player.posZ - LongJump.mc.player.prevPosZ) * (LongJump.mc.player.posZ - LongJump.mc.player.prevPosZ));
        if (lIIlIIlII(((boolean)this.accelerationTimer.getValue()) ? 1 : 0)) {
            LongJump.mc.timer.tickLength = 50.0f / this.timerSpeed.getValue();
        }
        else if (lIIlIIlII(lIIlIIIIl(LongJump.mc.timer.tickLength, 50.0f))) {
            LongJump.mc.timer.tickLength = 50.0f;
        }
        if (lIIlIIlIl(LongJump.mc.player.isSprinting() ? 1 : 0) && lIIlIIlII(((boolean)this.autoSprint.getValue()) ? 1 : 0)) {
            LongJump.mc.player.setSprinting(LongJump.llIllII[1] != 0);
            LongJump.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)LongJump.mc.player, CPacketEntityAction.Action.START_SPRINTING));
        }
    }
    
    private static int lIIlIIlll(final double var0, final double var2) {
        final double var3;
        return ((var3 = var0 - var2) == 0.0) ? 0 : ((var3 < 0.0) ? -1 : 1);
    }
    
    private static boolean isNull(final Object lllIIlIIlIIllIl) {
        return lllIIlIIlIIllIl == null;
    }
    
    private static boolean lIIlIIlII(final int lllIIlIIlIIlIll) {
        return lllIIlIIlIIlIll != 0;
    }
    
    @EventTarget
    public void onMove(final PlayerMoveEvent event) {
        if (event.getEventState() != Event.State.PRE) {
            return;
        }
        if (!isNull(LongJump.mc.player)) {
            final float currentTps = LongJump.mc.timer.tickLength / 1000.0f;
            switch (this.currentState) {
                case 0: {
                    this.currentState += LongJump.llIllII[1];
                    this.prevDist = 0.0;
                    break;
                }
                case 2: {
                    double lllIIlIIlllIlII = 0.40123128 + this.extraYBoost.getValue();
                    if ((!lIIlIIlIl(lIIlIIllI(LongJump.mc.player.moveForward, 0.0f)) || lIIlIIlII(lIIlIIllI(LongJump.mc.player.moveStrafing, 0.0f))) && lIIlIIlII(LongJump.mc.player.onGround ? 1 : 0)) {
                        if (lIIlIIlII(LongJump.mc.player.isPotionActive(MobEffects.JUMP_BOOST) ? 1 : 0) && lIIlIIlII(((boolean)this.jumpDetect.getValue()) ? 1 : 0)) {
                            lllIIlIIlllIlII += (LongJump.mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + LongJump.llIllII[1]) * 0.1f;
                        }
                        event.setY(LongJump.mc.player.motionY = lllIIlIIlllIlII);
                        this.motionSpeed *= 2.149;
                        break;
                    }
                    break;
                }
                case 3: {
                    this.motionSpeed = this.prevDist - 0.76 * (this.prevDist - this.getBaseMotionSpeed());
                    break;
                }
                default: {
                    if ((!lIIlIlIII(LongJump.mc.world.getCollisionBoxes((Entity)LongJump.mc.player, LongJump.mc.player.getEntityBoundingBox().offset(0.0, LongJump.mc.player.motionY, 0.0)).size()) || lIIlIIlII(LongJump.mc.player.collidedVertically ? 1 : 0)) && lIIlIlIIl(this.currentState)) {
                        if (lIIlIIlIl(lIIlIIllI(LongJump.mc.player.moveForward, 0.0f)) && lIIlIIlIl(lIIlIIllI(LongJump.mc.player.moveStrafing, 0.0f))) {
                            this.currentState = LongJump.llIllII[0];
                        }
                        else {
                            this.currentState = LongJump.llIllII[1];
                        }
                    }
                    this.motionSpeed = this.prevDist - this.prevDist / 159.0;
                    break;
                }
            }
            this.motionSpeed = Math.max(this.motionSpeed, this.getBaseMotionSpeed());
            double lllIIlIIlllIIIl = LongJump.mc.player.movementInput.moveForward;
            double lllIIlIIlllIIII = LongJump.mc.player.movementInput.moveStrafe;
            final double lllIIlIIllIllll = LongJump.mc.player.rotationYaw;
            if (lIIlIIlIl(lIIlIIlll(lllIIlIIlllIIIl, 0.0)) && lIIlIIlIl(lIIlIIlll(lllIIlIIlllIIII, 0.0))) {
                event.setX(0.0);
                event.setZ(0.0);
            }
            if (lIIlIIlII(lIIlIIlll(lllIIlIIlllIIIl, 0.0)) && lIIlIIlII(lIIlIIlll(lllIIlIIlllIIII, 0.0))) {
                lllIIlIIlllIIIl *= Math.sin(0.7853981633974483);
                lllIIlIIlllIIII *= Math.cos(0.7853981633974483);
            }
            event.setX((lllIIlIIlllIIIl * this.motionSpeed * -Math.sin(Math.toRadians(lllIIlIIllIllll)) + lllIIlIIlllIIII * this.motionSpeed * Math.cos(Math.toRadians(lllIIlIIllIllll))) * 0.99);
            event.setZ((lllIIlIIlllIIIl * this.motionSpeed * Math.cos(Math.toRadians(lllIIlIIllIllll)) - lllIIlIIlllIIII * this.motionSpeed * -Math.sin(Math.toRadians(lllIIlIIllIllll))) * 0.99);
            this.attempting = true;
            this.currentState += LongJump.llIllII[1];
        }
        event.setCancelled(true);
    }
    
    private static void lIIIllllI() {
        (LongJump.llIlIIl = new String[LongJump.llIllII[8]])[LongJump.llIllII[0]] = lIIIllIII("n9pHF6SFvkOs6iUr+fnXgA==", "GmCTC");
        LongJump.llIlIIl[LongJump.llIllII[1]] = lIIIllIII("4noHmwJ5F40+cu8qBPcyzA==", "CVFaT");
        LongJump.llIlIIl[LongJump.llIllII[2]] = lIIIllIII("R+hGwU+dCgQQcUdIkD9ZYaUO+QBhMxiN", "RjGgZ");
        LongJump.llIlIIl[LongJump.llIllII[3]] = lIIIllIII("Dk9SQuIPQSn5I8lWMj8Z+w==", "dWNML");
        LongJump.llIlIIl[LongJump.llIllII[5]] = lIIIllIII("rPWGh7vSeiSJWWJOJQfq5wdZ8fI6Y9G+", "QkkkG");
        LongJump.llIlIIl[LongJump.llIllII[6]] = lIIIllIII("6BSD78RsHX6yVgm/4JINjBgTGCxZfgXF", "rXpxu");
        LongJump.llIlIIl[LongJump.llIllII[7]] = lIIIlllIl("ENR8rJxJYtA86kRMf8iVlQ==", "RTxXY");
    }
    
    private double getBaseMotionSpeed() {
        double lllIIlIIllllllI = 0.272 * this.multiplier.getValue();
        if (lIIlIIlII(LongJump.mc.player.isPotionActive(MobEffects.SPEED) ? 1 : 0) && lIIlIIlII(((boolean)this.speedDetect.getValue()) ? 1 : 0)) {
            final int lllIIlIlIIIIIII = Objects.requireNonNull(LongJump.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            lllIIlIIllllllI *= 1.0 + 0.2 * lllIIlIlIIIIIII;
        }
        return lllIIlIIllllllI;
    }
    
    private static boolean lIIlIIlIl(final int lllIIlIIlIIlIIl) {
        return lllIIlIIlIIlIIl == 0;
    }
    
    private static String lIIIllIII(final String lllIIlIIlIlllll, final String lllIIlIIllIIIII) {
        try {
            final SecretKeySpec lllIIlIIllIIlII = new SecretKeySpec(MessageDigest.getInstance("MD5").digest(lllIIlIIllIIIII.getBytes(StandardCharsets.UTF_8)), "Blowfish");
            final Cipher lllIIlIIllIIIll = Cipher.getInstance("Blowfish");
            lllIIlIIllIIIll.init(LongJump.llIllII[2], lllIIlIIllIIlII);
            return new String(lllIIlIIllIIIll.doFinal(Base64.getDecoder().decode(lllIIlIIlIlllll.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
        }
        catch (Exception lllIIlIIllIIIlI) {
            lllIIlIIllIIIlI.printStackTrace();
            return null;
        }
    }
    
    private static String lIIIlllIl(final String lllIIlIIlIlIlII, final String lllIIlIIlIlIIIl) {
        try {
            final SecretKeySpec lllIIlIIlIlIlll = new SecretKeySpec(Arrays.copyOf(MessageDigest.getInstance("MD5").digest(lllIIlIIlIlIIIl.getBytes(StandardCharsets.UTF_8)), LongJump.llIllII[9]), "DES");
            final Cipher lllIIlIIlIlIllI = Cipher.getInstance("DES");
            lllIIlIIlIlIllI.init(LongJump.llIllII[2], lllIIlIIlIlIlll);
            return new String(lllIIlIIlIlIllI.doFinal(Base64.getDecoder().decode(lllIIlIIlIlIlII.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
        }
        catch (Exception lllIIlIIlIlIlIl) {
            lllIIlIIlIlIlIl.printStackTrace();
            return null;
        }
    }
    
    static {
        lIIlIIIII();
        lIIIllllI();
    }
}
