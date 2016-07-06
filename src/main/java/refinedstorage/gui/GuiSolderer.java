package refinedstorage.gui;

import refinedstorage.container.ContainerSolderer;
import refinedstorage.gui.sidebutton.SideButtonRedstoneMode;
import refinedstorage.tile.TileSolderer;

public class GuiSolderer extends GuiBase {
    private TileSolderer solderer;

    public GuiSolderer(ContainerSolderer container, TileSolderer solderer) {
        super(container, 211, 171);

        this.solderer = solderer;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(solderer));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/solderer.png");

        drawTexture(x, y, 0, 0, width, height);

        if (solderer.isWorking()) {
            drawTexture(x + 83, y + 38 - 1, 212, 0, solderer.getProgressScaled(22), 15);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedstorage:solderer"));
        drawString(7, 77, t("container.inventory"));

        if (inBounds(83, 38, 22, 15, mouseX, mouseY) && solderer.isWorking()) {
            drawTooltip(mouseX, mouseY, t("gui.refinedstorage:solderer.progress", solderer.getProgressScaled(100)));
        }
    }
}
