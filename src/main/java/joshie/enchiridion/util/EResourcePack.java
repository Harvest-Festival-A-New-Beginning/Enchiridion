package joshie.enchiridion.util;

import joshie.enchiridion.lib.EInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

public class EResourcePack implements PackResources {
    private final File root;

    public EResourcePack(File root) {
        this.root = root;
    }

    // TODO: getName() may have been removed from PackResources interface
    // @Override
    @Nonnull
    public String getName() {
        return EInfo.MODID;
    }

    @Override
    @Nonnull
    public String packId() {
        return EInfo.MODID;
    }

    @Override
    public void close() {
    }

    // TODO: PackResources interface completely changed in 1.20.4 - needs full rewrite
    @Override
    @Nullable
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) {
        return null;
    }

    // @Override
    @Nullable
    public IoSupplier<InputStream> getRootResource(String... elements) {
        return null;
    }

    // @Override
    @Nullable
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        return null;
    }

    // @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput resourceOutput) {
    }

    // @Override
    public Set<String> getNamespaces(PackType type) {
        return Collections.singleton(EInfo.MODID);
    }
}
