package net.lucab.shops.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.lucab.shops.block.custom.ShelfShopBlock;
import net.lucab.shops.block.entity.ShelfShopEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

public class ShelfShopEntityRenderer implements BlockEntityRenderer<ShelfShopEntity> {

    private final BlockEntityRendererProvider.Context context;

    public ShelfShopEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.context = ctx;
    }

    @Override
    public void render(ShelfShopEntity shop, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers,
            int light, int overlay) {

        final ShelfShopEntity.RendererData data1 = shop.rendererData();
        final ShelfShopEntity.RendererData data2 = shop.rendererDataTop();

        BlockState state = shop.getBlockState();
        if (!(state.getBlock() instanceof ShelfShopBlock))
            return;
        SlabType type = state.getValue(ShelfShopBlock.SHELVES_ENABLED);

        // global rotation and translation
        matrices.pushPose();
        matrices.translate(0.5f, 0.5f, 0.5f);

        matrices.mulPose(Axis.YP.rotationDegrees(
                switch (shop.getCachedFacingDirection()) {
                    case EAST -> 270f;
                    case SOUTH -> 180f;
                    case WEST -> 90f;
                    default -> 0f;
                }));// was rotated around 0,0,0

        // render bottom
        if (type == SlabType.BOTTOM || type == SlabType.DOUBLE) {
            renderShelf(data1, tickDelta, matrices, vertexConsumers, light, overlay, shop.furtherDataBottom());
        }

        // render top
        matrices.translate(0f, 0.44f, 0f);
        if (type == SlabType.TOP || type == SlabType.DOUBLE) {
            renderShelf(data2, tickDelta, matrices, vertexConsumers, light, overlay, shop.furtherDataTop());
        }

        matrices.popPose();

        if (type == SlabType.BOTTOM || type == SlabType.DOUBLE) {
            ShopRenderUtils.renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data1, context,
                    -1.05f,
                    0.3f, shop.getLevel());
        }

        if (type == SlabType.TOP || type == SlabType.DOUBLE) {
            ShopRenderUtils.renderShopWarns(tickDelta, matrices, vertexConsumers, light, overlay, data2, context, -0.6f,
                    0.3f, shop.getLevel());
        }

    }

    private void renderShelf(ShelfShopEntity.RendererData data, float tickDelta, PoseStack matrices,
            MultiBufferSource vertexConsumers, int light, int overlay, ShelfShopEntity.ShelfRenderData furtherData) {
        ItemDisplayContext mode;
        float itemTranslationFactor;
        Font font = this.context.getFont();
        Axis rotationAxis;
        int renderCount;
        if (data != null) {
            data.frameAccumulator();
            if (data.shopFunctional()) {

                // render item being sold
                matrices.pushPose();
                matrices.translate(0f, -0.216f, 0.3f);
                if (data.stockDisplayType()) {
                    // matrices.mulPose(Axis.YP.rotationDegrees(180.0f));
                    matrices.scale(0.2f, 0.2f, 0.2f);
                    mode = ItemDisplayContext.FIXED;
                    itemTranslationFactor = 1.15f;
                    rotationAxis = Axis.YP;
                    renderCount = 1;
                } else {
                    matrices.translate(0f, -0.08f, 0f);
                    matrices.mulPose(Axis.XP.rotationDegrees(90.0f));
                    matrices.scale(0.4f, 0.4f, 0.4f);
                    mode = ItemDisplayContext.GUI;
                    itemTranslationFactor = 0.5f;
                    rotationAxis = Axis.ZP;
                    renderCount = 3;
                }

                for (int y = 0; y < renderCount; y++) {
                    // left
                    matrices.pushPose();
                    matrices.translate(-itemTranslationFactor, 0f, -y * 0.05f);
                    matrices.mulPose(rotationAxis.rotationDegrees(furtherData.itemLrotation + (y + 1) * 55f));
                    this.context.getItemRenderer().renderStatic(data.displayItem(), mode,
                            light,
                            overlay,
                            matrices,
                            vertexConsumers,
                            null,
                            0);
                    matrices.popPose();

                    // right
                    matrices.pushPose();
                    matrices.translate(itemTranslationFactor, 0f, -y * 0.05f);
                    matrices.mulPose(rotationAxis.rotationDegrees(furtherData.itemRrotation + (y + 1) * 55f));
                    this.context.getItemRenderer().renderStatic(data.displayItem(), mode,
                            light,
                            overlay,
                            matrices,
                            vertexConsumers,
                            null,
                            0);
                    matrices.popPose();
                }
                matrices.popPose();

                if (data.stockDisplayType()) {
                    // for blocks
                    // render price (count of currency)

                    matrices.pushPose();
                    matrices.translate(-0.02f, -0.3124f, 0.16f);
                    matrices.mulPose(Axis.ZP.rotationDegrees(180f));// was rotated around 0,0,0
                    matrices.mulPose(Axis.XP.rotationDegrees(-90f));// was rotated around 0,0,0
                    matrices.scale(0.02f, 0.02f, -0.02f);
                    font.drawInBatch(
                            data.text(),
                            data.width(),
                            -4f,
                            0xffffff,
                            false,
                            matrices.last().pose(),
                            vertexConsumers,
                            Font.DisplayMode.NORMAL,
                            0,
                            light);
                    matrices.popPose();

                    // render amount being sold
                    matrices.pushPose();
                    matrices.translate(-0.02f, -0.17f, 0.43749f);
                    matrices.mulPose(Axis.ZP.rotationDegrees(180f));// was rotated around 0,0,0,
                    matrices.scale(0.022f, 0.022f, -0.022f);
                    font.drawInBatch(
                            data.stockQuantity,
                            data.qWidth(),
                            -4f,
                            0xffff00,
                            false,
                            matrices.last().pose(),
                            vertexConsumers,
                            Font.DisplayMode.NORMAL,
                            0x000000,
                            light);
                    matrices.popPose();

                    // render currency type
                    matrices.pushPose();
                    float scaleFactor;
                    matrices.mulPose(Axis.YP.rotationDegrees(180f));
                    if (data.currencyDisplayType()) {
                        // a block is being rendered
                        scaleFactor = 0.16f;
                        matrices.translate(0f, -0.265f, -0.35f);
                        matrices.mulPose(Axis.XP.rotationDegrees(-30f));
                    } else {
                        // an item is being rendered
                        scaleFactor = 0.35f;
                        matrices.translate(0f, -0.308f, -0.34f);
                        matrices.mulPose(Axis.XP.rotationDegrees(-90f));
                    }

                    matrices.scale(scaleFactor, scaleFactor, scaleFactor);
                    this.context.getItemRenderer().renderStatic(data.paymentItem(), ItemDisplayContext.GUI,
                            light,
                            overlay,
                            matrices,
                            vertexConsumers,
                            null,
                            0);
                    matrices.popPose();
                } else {
                    // render price (count of currency)

                    matrices.pushPose();
                    matrices.translate(-0.08f, -0.16f, 0.43749f);
                    matrices.mulPose(Axis.ZP.rotationDegrees(180f));// was rotated around 0,0,0
                    matrices.scale(0.016f, 0.016f, -0.016f);
                    font.drawInBatch(
                            data.text(),
                            data.width(),
                            -4f,
                            0xffffff,
                            false,
                            matrices.last().pose(),
                            vertexConsumers,
                            Font.DisplayMode.NORMAL,
                            0,
                            light);
                    matrices.popPose();

                    // render amount being sold
                    matrices.pushPose();
                    matrices.translate(0.2f, -0.16f, 0.43749f);
                    matrices.mulPose(Axis.ZP.rotationDegrees(180f)); // was rotated around 0,0,0
                    matrices.scale(0.016f, 0.016f, -0.016f);
                    font.drawInBatch(
                            data.stockQuantity,
                            data.qWidth(),
                            -4f,
                            0xffff00,
                            false,
                            matrices.last().pose(),
                            vertexConsumers,
                            Font.DisplayMode.NORMAL,
                            0x000000,
                            light);
                    matrices.popPose();

                    // render currency type
                    matrices.pushPose();
                    matrices.translate(-0.245f, -0.16f, 0.43f);
                    float scaleFactor;
                    if (data.currencyDisplayType()) {
                        // a block is being rendered
                        matrices.mulPose(Axis.XP.rotationDegrees(20f));
                        scaleFactor = 0.16f;
                    } else {
                        // an item is being rendered
                        scaleFactor = 0.22f;
                    }

                    matrices.mulPose(Axis.YP.rotationDegrees(180f));

                    matrices.scale(scaleFactor, scaleFactor, scaleFactor);
                    this.context.getItemRenderer().renderStatic(data.paymentItem(), ItemDisplayContext.GUI,
                            light,
                            overlay,
                            matrices,
                            vertexConsumers,
                            null,
                            0);
                    matrices.popPose();
                }
            }
        }
    }
}
