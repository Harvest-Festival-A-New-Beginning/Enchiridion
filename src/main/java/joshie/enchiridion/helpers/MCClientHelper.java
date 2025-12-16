package joshie.enchiridion.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageInfo;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.lwjgl.glfw.GLFW;

public class MCClientHelper {
    public static boolean isShiftPressed() {
        long handle = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_SHIFT) || InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static LanguageInfo getLang() {
        return Minecraft.getInstance().getLanguageManager().getSelected();
    }

    public static boolean isCtrlPressed() {
        long handle = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_LEFT_CONTROL) || InputConstants.isKeyDown(handle, GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static Player getPlayer() {
        return Minecraft.getInstance().player;
    }

    public static Level getWorld() {
        return Minecraft.getInstance().level();
    }
}