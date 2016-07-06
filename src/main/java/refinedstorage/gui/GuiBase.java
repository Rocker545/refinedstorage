package refinedstorage.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import refinedstorage.RefinedStorage;
import refinedstorage.gui.sidebutton.SideButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class GuiBase extends GuiContainer {
    protected static final int SIDE_BUTTON_WIDTH = 20;
    protected static final int SIDE_BUTTON_HEIGHT = 20;

    private List<SideButton> sideButtons = new ArrayList<SideButton>();

    private int lastButtonId = 0;
    private int lastSideButtonY = 6;

    private Scrollbar scrollbar;

    protected int width;
    protected int height;

    public GuiBase(Container container, int width, int height) {
        super(container);

        this.width = width;
        this.height = height;
        this.xSize = width;
        this.ySize = height;
    }

    public void setScrollbar(Scrollbar scrollbar) {
        this.scrollbar = scrollbar;
    }

    public Scrollbar getScrollbar() {
        return scrollbar;
    }

    @Override
    public void initGui() {
        if (sideButtons.size() > 0) {
            xSize -= SIDE_BUTTON_WIDTH;
        }

        super.initGui();

        sideButtons.clear();

        lastButtonId = 0;
        lastSideButtonY = 6;

        init(guiLeft, guiTop);

        if (sideButtons.size() > 0) {
            xSize += SIDE_BUTTON_WIDTH;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        update(guiLeft, guiTop);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (scrollbar != null) {
            scrollbar.update(this, mouseX - guiLeft, mouseY - guiTop);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        drawBackground(guiLeft, guiTop, mouseX, mouseY);

        if (scrollbar != null) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            scrollbar.draw(this);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        mouseX -= guiLeft;
        mouseY -= guiTop;

        String sideButtonTooltip = null;

        for (SideButton sideButton : sideButtons) {
            sideButton.draw(this, sideButton.getX() + 2, sideButton.getY() + 1);

            if (inBounds(sideButton.getX(), sideButton.getY(), SIDE_BUTTON_WIDTH, SIDE_BUTTON_HEIGHT, mouseX, mouseY)) {
                sideButtonTooltip = sideButton.getTooltip(this);
            }
        }

        drawForeground(mouseX, mouseY);

        if (sideButtonTooltip != null) {
            drawTooltip(mouseX, mouseY, sideButtonTooltip);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int d = Mouse.getEventDWheel();

        if (scrollbar != null && d != 0) {
            scrollbar.wheel(d);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        for (SideButton sideButton : sideButtons) {
            if (sideButton.getId() == button.id) {
                sideButton.actionPerformed();
            }
        }
    }

    public GuiButton addButton(int x, int y, int w, int h) {
        return addButton(x, y, w, h, "");
    }

    public GuiButton addButton(int x, int y, int w, int h, String text) {
        return addButton(x, y, w, h, text, true);
    }

    public GuiButton addButton(int x, int y, int w, int h, String text, boolean enabled) {
        GuiButton button = new GuiButton(lastButtonId++, x, y, w, h, text);
        button.enabled = enabled;

        buttonList.add(button);

        return button;
    }

    public void addSideButton(SideButton button) {
        button.setX(-SIDE_BUTTON_WIDTH + 1);
        button.setY(lastSideButtonY);
        button.setId(addButton(guiLeft + button.getX(), guiTop + button.getY(), SIDE_BUTTON_WIDTH, SIDE_BUTTON_HEIGHT).id);

        lastSideButtonY += SIDE_BUTTON_HEIGHT + 4;

        sideButtons.add(button);
    }

    public boolean inBounds(int x, int y, int w, int h, int ox, int oy) {
        return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
    }

    public void bindTexture(String file) {
        bindTexture(RefinedStorage.ID, file);
    }

    public void bindTexture(String base, String file) {
        mc.getTextureManager().bindTexture(new ResourceLocation(base, "textures/" + file));
    }

    public void drawItem(int x, int y, ItemStack stack) {
        drawItem(x, y, stack, false);
    }

    public void drawItem(int x, int y, ItemStack stack, boolean withOverlay) {
        drawItem(x, y, stack, withOverlay, null);
    }

    public void drawItem(int x, int y, ItemStack stack, boolean withOverlay, String message) {
        zLevel = 200.0F;
        itemRender.zLevel = 200.0F;

        itemRender.renderItemIntoGUI(stack, x, y);

        if (withOverlay) {
            drawItemOverlay(stack, message, x, y);
        }

        zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
    }

    public void drawItemOverlay(ItemStack stack, String text, int x, int y) {
        itemRender.renderItemOverlayIntoGUI(fontRendererObj, stack, x, y, "");

        if (text != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 1);
            GlStateManager.scale(0.5f, 0.5f, 1);

            GlStateManager.disableLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.disableDepth();

            fontRendererObj.drawStringWithShadow(text, 30 - fontRendererObj.getStringWidth(text), 22, 16777215);

            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public void drawString(int x, int y, String message) {
        drawString(x, y, message, 4210752);
    }

    public void drawString(int x, int y, String message, int color) {
        GlStateManager.disableLighting();
        fontRendererObj.drawString(message, x, y, color);
        GlStateManager.enableLighting();
    }

    public void drawTooltip(int x, int y, String message) {
        drawTooltip(x, y, Arrays.asList(message.split("\n")));
    }

    public void drawTooltip(int x, int y, List<String> lines) {
        GlStateManager.disableLighting();
        drawHoveringText(lines, x, y);
        GlStateManager.enableLighting();
    }

    public void drawTooltip(int x, int y, ItemStack stack) {
        GlStateManager.disableLighting();
        renderToolTip(stack, x, y);
        GlStateManager.enableLighting();
    }

    public void drawTexture(int x, int y, int textureX, int textureY, int width, int height) {
        drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    public String t(String name, Object... format) {
        return I18n.format(name, format);
    }

    public abstract void init(int x, int y);

    public abstract void update(int x, int y);

    public abstract void drawBackground(int x, int y, int mouseX, int mouseY);

    public abstract void drawForeground(int mouseX, int mouseY);

    public int getGuiLeft() {
        return guiLeft;
    }

    public int getGuiTop() {
        return guiTop;
    }
}
