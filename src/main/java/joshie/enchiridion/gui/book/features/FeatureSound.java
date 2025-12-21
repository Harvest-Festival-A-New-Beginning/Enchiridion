package joshie.enchiridion.gui.book.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.data.book.FeatureProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;

public class FeatureSound extends FeatureProvider {
    public enum TriggerType implements StringRepresentable {
        HOVER("hover"),
        CLICK("click"),
        ALWAYS("always");

        private final String name;

        TriggerType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final Codec<FeatureSound> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceLocation.CODEC.fieldOf("sound").forGetter(f -> f.soundId),
        StringRepresentable.fromEnum(TriggerType::values).optionalFieldOf("trigger", TriggerType.CLICK).forGetter(f -> f.trigger),
        Codec.FLOAT.optionalFieldOf("volume", 1.0F).forGetter(f -> f.volume),
        Codec.FLOAT.optionalFieldOf("pitch", 1.0F).forGetter(f -> f.pitch),
        Codec.BOOL.optionalFieldOf("loop", false).forGetter(f -> f.loop),
        Codec.INT.optionalFieldOf("cooldown", 0).forGetter(f -> f.cooldown)
    ).apply(instance, (sound, trigger, volume, pitch, loop, cooldown) -> {
        FeatureSound feature = new FeatureSound();
        feature.soundId = sound;
        feature.trigger = trigger;
        feature.volume = volume;
        feature.pitch = pitch;
        feature.loop = loop;
        feature.cooldown = cooldown;
        return feature;
    }));

    public ResourceLocation soundId;
    public TriggerType trigger = TriggerType.CLICK;
    public float volume = 1.0F;
    public float pitch = 1.0F;
    public boolean loop = false;
    public int cooldown = 0; // Cooldown in ticks between plays

    private transient SoundEvent cachedSound = null;
    private transient boolean failedToLoad = false;
    private transient boolean wasHovering = false;
    private transient long lastPlayTime = 0;

    public FeatureSound() {
        super(0, 0, 0, 0);
    }

    public FeatureSound(ResourceLocation soundId) {
        super(0, 0, 0, 0);
        this.soundId = soundId;
    }

    @Override
    public FeatureProvider copy() {
        FeatureSound sound = new FeatureSound(soundId);
        sound.trigger = trigger;
        sound.volume = volume;
        sound.pitch = pitch;
        sound.loop = loop;
        sound.cooldown = cooldown;
        return sound;
    }

    @Override
    public String getName() {
        if (soundId != null) {
            return "Sound: " + soundId.getPath();
        }
        return "Sound: unknown";
    }

    @Override
    public void update(joshie.enchiridion.api.book.IPage page) {
        super.update(page);
        cachedSound = null;
        failedToLoad = false;
    }

    private SoundEvent getSound() {
        if (failedToLoad) return null;
        if (cachedSound != null) return cachedSound;

        try {
            cachedSound = BuiltInRegistries.SOUND_EVENT.get(soundId);
            if (cachedSound == null) {
                failedToLoad = true;
                return null;
            }
            return cachedSound;
        } catch (Exception e) {
            failedToLoad = true;
            return null;
        }
    }

    private boolean canPlay() {
        if (cooldown <= 0) return true;
        long currentTime = System.currentTimeMillis();
        long cooldownMs = cooldown * 50; // Convert ticks to milliseconds
        return (currentTime - lastPlayTime) >= cooldownMs;
    }

    private void playSound() {
        if (!canPlay()) return;

        SoundEvent sound = getSound();
        if (sound == null) return;

        Minecraft mc = Minecraft.getInstance();
        SimpleSoundInstance soundInstance = new SimpleSoundInstance(
            sound.getLocation(),
            SoundSource.MASTER,
            volume,
            pitch,
            mc.level.getRandom(),
            loop,
            0,
            SoundInstance.Attenuation.NONE,
            0, 0, 0,
            true
        );

        mc.getSoundManager().play(soundInstance);
        lastPlayTime = System.currentTimeMillis();
    }

    @Override
    protected void drawFeature(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // Handle hover trigger
        if (trigger == TriggerType.HOVER) {
            boolean isHovering = isOverFeature(mouseX, mouseY);
            if (isHovering && !wasHovering) {
                playSound();
            }
            wasHovering = isHovering;
        }

        // Handle always trigger
        if (trigger == TriggerType.ALWAYS && canPlay()) {
            playSound();
        }

        // Optional: Draw a speaker icon to indicate sound feature
        // (commented out by default - users can enable if needed)
        /*
        if (isOverFeature(mouseX, mouseY)) {
            int centerX = (getX() + getRight()) / 2;
            int centerY = (getY() + getBottom()) / 2;
            guiGraphics.drawCenteredString(
                Minecraft.getInstance().font,
                "♪",
                centerX,
                centerY,
                0xFFFFFF
            );
        }
        */
    }

    @Override
    public boolean performClick(int mouseX, int mouseY, int button, Object gui) {
        if (trigger == TriggerType.CLICK && isOverFeature(mouseX, mouseY)) {
            playSound();
            return true;
        }
        return false;
    }

    @Override
    public Codec<? extends FeatureProvider> codec() {
        return CODEC;
    }
}
