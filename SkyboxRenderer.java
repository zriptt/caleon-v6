package com.caleon.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Draws a big textured cube around the camera (same image on all six faces) in place of the
 * vanilla sky. NOTE: the exact shader/vertex API names can shift a little between Minecraft
 * versions - if this fails to compile, check what your mapped MinecraftClient/BufferBuilder
 * actually expose and adjust the calls below to match; everything else in the mod is unaffected.
 */
public class SkyboxRenderer {
    private static final Identifier TEX_ID = Identifier.of("caleonclient", "skybox_custom");
    private static boolean loaded;

    public static boolean ready() { return loaded; }

    public static boolean load(Path file) {
        try (InputStream in = Files.newInputStream(file)) {
            NativeImage img = NativeImage.read(in);
            MinecraftClient.getInstance().getTextureManager().registerTexture(TEX_ID, new NativeImageBackedTexture(img));
            loaded = true;
            return true;
        } catch (IOException e) {
            loaded = false;
            return false;
        }
    }

    public static void draw(Matrix4f positionMatrix) {
        if (!loaded) return;

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
        RenderSystem.setShaderTexture(0, TEX_ID);
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        BufferAllocator alloc = new BufferAllocator(1 << 15);
        BufferBuilder bb = new BufferBuilder(alloc, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        float d = 400f;

        // +X, -X, +Y (top), -Y (bottom), +Z, -Z - each face gets the whole image.
        quad(bb, positionMatrix, d, -d, -d, d, -d, d, d, d, d, d, d, -d);
        quad(bb, positionMatrix, -d, -d, d, -d, d, d, -d, d, -d, -d, -d, -d);
        quad(bb, positionMatrix, -d, d, -d, -d, d, d, d, d, d, d, d, -d);
        quad(bb, positionMatrix, -d, -d, d, -d, -d, -d, d, -d, -d, d, -d, d);
        quad(bb, positionMatrix, -d, -d, -d, d, -d, -d, d, d, -d, -d, d, -d);
        quad(bb, positionMatrix, d, -d, d, -d, -d, d, -d, d, d, d, d, d);

        BuiltBuffer built = bb.endNullable();
        if (built != null) BufferRenderer.drawWithGlobalProgram(built);

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }

    private static void quad(BufferBuilder bb, Matrix4f m,
                              float x1, float y1, float z1, float x2, float y2, float z2,
                              float x3, float y3, float z3, float x4, float y4, float z4) {
        bb.vertex(m, x1, y1, z1).texture(0, 0);
        bb.vertex(m, x2, y2, z2).texture(1, 0);
        bb.vertex(m, x3, y3, z3).texture(1, 1);
        bb.vertex(m, x4, y4, z4).texture(0, 1);
    }
}
