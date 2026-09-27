package net.congling.allinzero.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.congling.allinzero.Allinzero;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;

/**
 * 透灵从零的自定义 BOSS 血条：
 * 使用 textures/gui 下的 touling_lord_bar_empty（深色底槽）与 touling_lord_bar_frame（青色边框）两张贴图，
 * 血量以青色填充条在底槽内按比例裁切显示。
 */
@EventBusSubscriber(modid = Allinzero.MODID, value = Dist.CLIENT)
public class ToulingLordBarOverlay {

    private static final ResourceLocation BAR_EMPTY =
            ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, "textures/gui/touling_lord_bar_empty.png");
    private static final ResourceLocation BAR_FRAME =
            ResourceLocation.fromNamespaceAndPath(Allinzero.MODID, "textures/gui/touling_lord_bar_frame.png");

    /** 透灵从零的实体名翻译键（ServerBossEvent 以此作为标题） */
    private static final String BOSS_NAME_KEY = "entity.allinzero.touling_congling";

    private static final int TEXTURE_WIDTH = 226;
    private static final int TEXTURE_HEIGHT = 44;

    // 贴图中内部血槽的像素范围（x=16..210, y=23..27，共 195x5），填充向内缩进 1px 避免盖住圆角
    private static final int FILL_INSET_LEFT = 17;
    private static final int FILL_MAX_WIDTH = 193;
    private static final int FILL_TOP = 24;
    private static final int FILL_BOTTOM = 27;

    /** 填充条青色（取自边框/菱形宝石的青色） */
    private static final int FILL_COLOR = 0xFF42D9E8;
    private static final int FILL_HIGHLIGHT_COLOR = 0xFF9BF4FF;

    @SubscribeEvent
    public static void onBossEventProgress(CustomizeGuiOverlayEvent.BossEventProgress event) {
        Component name = event.getBossEvent().getName();
        if (!(name.getContents() instanceof TranslatableContents translatable)
                || !BOSS_NAME_KEY.equals(translatable.getKey())) {
            return;
        }

        event.setCanceled(true);

        GuiGraphics graphics = event.getGuiGraphics();
        Minecraft minecraft = Minecraft.getInstance();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();

        // 原版血条宽 182 从 getX() 开始；本贴图宽 226，整体居中需要左移 22px
        int left = event.getX() - (TEXTURE_WIDTH - 182) / 2;
        int top = event.getY();

        // 1. 底槽
        RenderSystem.enableBlend();
        graphics.blit(BAR_EMPTY, left, top, 0.0F, 0.0F,
                TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        // 2. 血量填充（按比例裁切，不拉伸贴图）
        float progress = Math.max(0.0F, Math.min(1.0F, event.getBossEvent().getProgress()));
        int filled = Math.round(progress * FILL_MAX_WIDTH);
        if (filled > 0) {
            int fillLeft = left + FILL_INSET_LEFT;
            graphics.fill(fillLeft, top + FILL_TOP, fillLeft + filled, top + FILL_BOTTOM, FILL_COLOR);
            // 顶部 1px 高光
            graphics.fill(fillLeft, top + FILL_TOP, fillLeft + filled, top + FILL_TOP + 1, FILL_HIGHLIGHT_COLOR);
        }

        // 3. 边框覆盖在最上层
        graphics.blit(BAR_FRAME, left, top, 0.0F, 0.0F,
                TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        RenderSystem.disableBlend();

        // 4. BOSS 名字（沿用原版：血条上方居中）
        int nameWidth = minecraft.font.width(name);
        graphics.drawString(minecraft.font, name,
                screenWidth / 2 - nameWidth / 2, top - 10, 0xFFFFFFFF, true);

        // 本条血条占用的纵向高度（贴图 44px + 与下一条的间隔）
        event.setIncrement(56);
    }
}
