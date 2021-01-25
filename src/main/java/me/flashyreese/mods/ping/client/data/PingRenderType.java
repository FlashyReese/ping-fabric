package me.flashyreese.mods.ping.client.data;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class PingRenderType extends RenderPhase {
    protected static final RenderPhase.Layering DISABLE_DEPTH = new RenderPhase.Layering("disable_depth", GlStateManager::disableDepthTest, GlStateManager::enableDepthTest);

    public PingRenderType(String string, Runnable r, Runnable r1) {
        super(string, r, r1);
    }

    public static RenderLayer getPingOverlay() {
        RenderLayer.MultiPhaseParameters renderTypeState = RenderLayer.MultiPhaseParameters.builder().transparency(TRANSLUCENT_TRANSPARENCY).texture(BLOCK_ATLAS_TEXTURE).layering(DISABLE_DEPTH).build(true);
        return RenderLayer.of("ping_overlay", VertexFormats.POSITION_TEXTURE_COLOR, 7, 262144, true, true, renderTypeState);
    }

    public static RenderLayer getPingIcon(Identifier location) {
        RenderLayer.MultiPhaseParameters renderTypeState = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(location, false, true)).transparency(TRANSLUCENT_TRANSPARENCY).layering(DISABLE_DEPTH).build(true);
        return RenderLayer.of("ping_icon", VertexFormats.POSITION_TEXTURE_COLOR, 7, 262144, true, true, renderTypeState);
    }
}