package pers.wjx.plugin;

import com.intellij.openapi.ui.GraphicsConfig;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.GraphicsUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.apache.commons.lang3.ObjectUtils;
import pers.wjx.plugin.progress.state.ProgressBarSettingState;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class ProgressBarUI extends BasicProgressBarUI {
    private static final float ONE_OVER_FOUR = 1f / 4;

    public ProgressBarUI() {
    }

    @SuppressWarnings({"MethodOverridesStaticMethodOfSuperclass", "UnusedDeclaration"})
    public static ComponentUI createUI(JComponent c) {
        c.setBorder(JBUI.Borders.empty().asUIResource());
        return new ProgressBarUI();
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(super.getPreferredSize(c).width, JBUIScale.scale(20));
    }

    private int offset = 0;
    private volatile int velocity = 1;

    @Override
    protected void paintIndeterminate(Graphics g2d, JComponent c) {
        if (!(g2d instanceof Graphics2D)) {
            return;
        }
        Graphics2D g = (Graphics2D) g2d;
        Insets b = progressBar.getInsets();
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);
        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return;
        }
        ProgressBarSettingState setting = ProgressBarSettingState.getInstance();
        g.setColor(new JBColor(Gray._240.withAlpha(50), Gray._128.withAlpha(50)));
        int w = c.getWidth();
        int h = c.getPreferredSize().height;
        if (isEven(c.getHeight() - h)) h++;

        if (c.isOpaque()) {
            g.fillRect(0, (c.getHeight() - h) / 2, w, h);
        }
        g.setColor(new JBColor(Gray._165.withAlpha(50), Gray._88.withAlpha(50)));
        final GraphicsConfig config = GraphicsUtil.setupAAPainting(g);
        g.translate(0, (c.getHeight() - h) / 2);

        if (ObjectUtils.isEmpty(setting.getTrack())) {
            g.setPaint(getLinearGradientPaint(h));
        } else {
            TexturePaint tp = new TexturePaint(setting.getTrack(), new Rectangle2D.Double(0, 1, h - 2f, h - 2f));
            g.setPaint(tp);
        }

        final float R = JBUIScale.scale(8f);
        final float R2 = JBUIScale.scale(9f);
        final Area containingRoundRect = new Area(new RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, R, R));
        g.fill(containingRoundRect);

        offset += velocity;
        if (offset <= 2) {
            offset = 2;
            velocity = 1;
        } else if (offset >= w - JBUIScale.scale(10)) {
            offset = w - JBUIScale.scale(10);
            velocity = -1;
        }
        Area area = new Area(new Rectangle2D.Float(0, 0, w, h));
        area.subtract(new Area(new RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, R, R)));
        if (c.isOpaque()) {
            g.fill(area);
        }
        area.subtract(new Area(new RoundRectangle2D.Float(0, 0, w, h, R2, R2)));
        c.getParent();
        if (c.isOpaque()) {
            g.fill(area);
        }

        Icon icon;
        if (velocity > 0) {
            icon = setting.getIcon();
        } else {
            icon = setting.getHorizontalFlip() ? setting.getHorizontal() : setting.getIcon();
        }
        icon.paintIcon(progressBar, g, offset - JBUI.scale(5), JBUI.scale(1));

        g.draw(new RoundRectangle2D.Float(1f, 1f, w - 2f - 1f, h - 2f - 1f, R, R));
        g.translate(0, -(c.getHeight() - h) / 2);

        if (progressBar.isStringPainted()) {
            if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, boxRect.x, boxRect.width);
            } else {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, boxRect.y, boxRect.height);
            }
        }
        config.restore();
    }

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        if (!(g instanceof Graphics2D)) {
            return;
        }

        if (progressBar.getOrientation() != SwingConstants.HORIZONTAL || !c.getComponentOrientation().isLeftToRight()) {
            super.paintDeterminate(g, c);
            return;
        }
        final GraphicsConfig config = GraphicsUtil.setupAAPainting(g);
        Insets b = progressBar.getInsets();
        int w = progressBar.getWidth();
        int h = progressBar.getPreferredSize().height;
        if (isEven(c.getHeight() - h)) h++;
        int barRectWidth = w - (b.right + b.left);
        int barRectHeight = h - (b.top + b.bottom);
        if (barRectWidth <= 0 || barRectHeight <= 0) {
            return;
        }
        int amountFull = getAmountFull(b, barRectWidth, barRectHeight);
        Container parent = c.getParent();
        Color background = parent != null ? parent.getBackground() : UIUtil.getPanelBackground();
        g.setColor(background);
        Graphics2D g2 = (Graphics2D) g;
        if (c.isOpaque()) {
            g.fillRect(0, 0, w, h);
        }

        final float R = JBUIScale.scale(8f);
        final float R2 = JBUIScale.scale(9f);
        final float off = JBUIScale.scale(1f);
        g2.translate(0, (c.getHeight() - h) / 2);
        g2.setColor(progressBar.getForeground());
        g2.fill(new RoundRectangle2D.Float(0, 0, w - off, h - off, R2, R2));
        g2.setColor(background);
        g2.fill(new RoundRectangle2D.Float(off, off, w - 2f * off - off, h - 2f * off - off, R, R));

        g2.translate(0, -(c.getHeight() - h) / 2);
        ProgressBarSettingState settingState = ProgressBarSettingState.getInstance();
        if (settingState.getTrack() != null) {
            TexturePaint tp = new TexturePaint(settingState.getTrack(), new Rectangle2D.Double(0, 1, h - 2f * off - off, h - 2f * off - off));
            g2.setPaint(tp);
        } else {
            g2.setPaint(getLinearGradientPaint(h));
        }
        settingState.getIcon().paintIcon(progressBar, g2, amountFull - JBUIScale.scale(5), -JBUIScale.scale(1));
        g2.fill(new RoundRectangle2D.Float(2f * off, 2f * off,
                amountFull - JBUIScale.scale(5f), h - JBUIScale.scale(5f),
                JBUIScale.scale(7f), JBUIScale.scale(7f)));
        g2.translate(0, -(c.getHeight() - h) / 2);

        if (progressBar.isStringPainted()) {
            paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);
        }
        config.restore();
    }

    private void paintString(Graphics g, int x, int y, int w, int h, int fillStart, int amountFull) {
        if (!(g instanceof Graphics2D)) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        String progressString = progressBar.getString();
        g2.setFont(progressBar.getFont());
        Point renderLocation = getStringPlacement(g2, progressString, x, y, w, h);
        Rectangle oldClip = g2.getClipBounds();

        if (progressBar.getOrientation() == SwingConstants.HORIZONTAL) {
            g2.setColor(getSelectionBackground());
            BasicGraphicsUtils.drawString(progressBar, g2, progressString, renderLocation.x, renderLocation.y);

            g2.setColor(getSelectionForeground());
            g2.clipRect(fillStart, y, amountFull, h);
            BasicGraphicsUtils.drawString(progressBar, g2, progressString, renderLocation.x, renderLocation.y);
        } else {
            g2.setColor(getSelectionBackground());
            AffineTransform rotate = AffineTransform.getRotateInstance(Math.PI / 2);
            g2.setFont(progressBar.getFont().deriveFont(rotate));
            renderLocation = getStringPlacement(g2, progressString, x, y, w, h);
            BasicGraphicsUtils.drawString(progressBar, g2, progressString, renderLocation.x, renderLocation.y);
            g2.setColor(getSelectionForeground());
            g2.clipRect(x, fillStart, w, amountFull);
            BasicGraphicsUtils.drawString(progressBar, g2, progressString, renderLocation.x, renderLocation.y);
        }
        g2.setClip(oldClip);
    }

    @Override
    protected int getBoxLength(int availableLength, int otherDimension) {
        return availableLength;
    }

    private LinearGradientPaint getLinearGradientPaint(int h) {
        return new LinearGradientPaint(0, JBUI.scale(2), 0, h - JBUI.scale(6),
                new float[]{ONE_OVER_FOUR * 1, ONE_OVER_FOUR * 2, ONE_OVER_FOUR * 3, ONE_OVER_FOUR * 4},
                new Color[]{
                        new JBColor(new Color(90, 202, 206), new Color(90, 202, 206)),
                        new JBColor(new Color(253, 239, 81), new Color(253, 239, 81)),
                        new JBColor(new Color(217, 51, 138), new Color(217, 51, 138)),
                        new JBColor(new Color(130, 206, 238), new Color(130, 206, 238)),
                }
        );
    }

    private static boolean isEven(int value) {
        return value % 2 != 0;
    }
}
