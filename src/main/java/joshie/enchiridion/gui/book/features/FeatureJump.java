package joshie.enchiridion.gui.book.features;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.gui.book.GuiBook;
import joshie.enchiridion.helpers.JSONHelper;
import joshie.enchiridion.helpers.JumpHelper;

public class FeatureJump extends FeatureAbstract {
    public static final Codec<FeatureJump> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("number", 0).forGetter(f -> f.number),
        Codec.STRING.optionalFieldOf("jumpTo", "#LEGACY#").forGetter(f -> f.jumpTo)
    ).apply(instance, (number, jumpTo) -> {
        FeatureJump feature = new FeatureJump();
        feature.number = number;
        feature.jumpTo = jumpTo;
        return feature;
    }));

    public transient IPage page;
    protected transient int number;
    protected transient String jumpTo;

    public FeatureJump() {
    }

    public FeatureJump(int number, String jumpTo) {
        this.number = number;
        this.jumpTo = jumpTo;
    }

    @Override
    public FeatureJump copy() {
        return new FeatureJump(number, jumpTo);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (page == null) {
            if (jumpTo != null && !jumpTo.equals("#LEGACY#")) {
                try {
                    page = JumpHelper.getPageByNumber(GuiBook.INSTANCE.getBook(), Integer.parseInt(jumpTo));
                } catch (Exception ignored) {
                }
            } else {
                page = JumpHelper.getPageByNumber(GuiBook.INSTANCE.getBook(), number);
            }
        }
    }

    @Override
    public boolean performClick(int mouseX, int mouseY, int button) {
        return EnchiridionAPI.book.jumpToPageIfExists(page.getPageNumber());
    }

    @Override
    public void readFromJson(JsonObject json) {
        number = JSONHelper.getIntegerIfExists(json, "number");
        if (json.get("jumpTo") != null) {
            jumpTo = JSONHelper.getStringIfExists(json, "jumpTo");
        } else jumpTo = "#LEGACY#";
    }

    @Override
    public void writeToJson(JsonObject object) {
        if (page != null) {
            object.addProperty("number", page.getPageNumber());
        }
    }

    @Override
    public Codec<? extends joshie.enchiridion.api.book.IFeature> getCodec() {
        return CODEC;
    }
}