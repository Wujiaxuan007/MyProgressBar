package pers.wjx.plugin;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.UI;
import org.apache.commons.lang3.ObjectUtils;
import pers.wjx.plugin.progress.common.Icons;
import pers.wjx.plugin.progress.common.ProgressBarBundle;
import pers.wjx.plugin.progress.state.BufferedImageInfo;
import pers.wjx.plugin.progress.state.ImageIconInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author wjx
 */
public class ProgressBarConfigForm {
    private JPanel iconPreview;
    private JPanel trackPane;
    private JButton chooseBg;
    private JButton chooseIcon;
    private JPanel rootPane;
    private JCheckBox horizontalFlip;
    private JCheckBox useDefaultIcon;
    private JPanel customIconPane;
    private JPanel iconTipPane;
    private JPanel iconPanel;
    private JPanel trackPanel;
    private JPanel trackTipPane;
    private JPanel horizontalFlipTipPane;
    private JPanel customTrackPane;
    private JCheckBox useDefaultTrack;
    private JLabel iconLabel;
    private JLabel trackLabel;
    private ImageIcon track;
    private final AtomicReference<VirtualFile> trackFile = new AtomicReference<>(null);
    private final AtomicReference<VirtualFile> iconFile = new AtomicReference<>(null);

    public ProgressBarConfigForm(ImageIconInfo iconInfo, BufferedImageInfo trackInfo, Icon icon,
                                 BufferedImage track, boolean useDefaultIcon, boolean useDefaultTrack) {
        initComponent(iconInfo, trackInfo, icon, track, useDefaultIcon, useDefaultTrack);
    }

    public JPanel getPanel() {
        return rootPane;
    }

    public Boolean getUseDefaultIcon() {
        return useDefaultIcon.isSelected();
    }

    public Boolean getUseDefaultTrack() {
        return useDefaultTrack.isSelected();
    }

    public void setUseDefaultIcon(Boolean useDefault) {
        this.useDefaultIcon.setSelected(useDefault);
    }

    public void setUseDefaultTrack(Boolean useDefault) {
        this.useDefaultTrack.setSelected(useDefault);
    }


    public Boolean getHorizontalFlip() {
        return horizontalFlip.isSelected();
    }

    public void setHorizontalFlip(Boolean useDefault) {
        this.horizontalFlip.setSelected(useDefault);
    }

    public AtomicReference<VirtualFile> getTrackFile() {
        return trackFile;
    }

    public AtomicReference<VirtualFile> getIconFile() {
        return iconFile;
    }

    public Icon getIcon() {
        return iconLabel.getIcon();
    }

    public Icon getHorizontalIcon() {
        if (horizontalFlip.isSelected() && iconLabel.getIcon() != null) {
            return IconUtil.flip(iconLabel.getIcon(), true);
        }
        return null;
    }

    public Icon getTrack() {
        return trackLabel.getIcon();
    }

    /**
     * 初始化组件
     */
    private void initComponent(ImageIconInfo iconInfo, BufferedImageInfo trackInfo, Icon icon,
                               BufferedImage track, boolean useDefaultIcon, boolean useDefaultTrack) {
        if (trackInfo != null && ObjectUtils.isNotEmpty(trackInfo.getPath())) {
            VirtualFile virtualFile = VirtualFileManager.getInstance().refreshAndFindFileByNioPath(Path.of(trackInfo.getPath()));
            trackFile.set(virtualFile);
        }
        if (iconInfo != null && ObjectUtils.isNotEmpty(iconInfo.getPath())) {
            VirtualFile virtualFile = VirtualFileManager.getInstance().refreshAndFindFileByNioPath(Path.of(iconInfo.getPath()));
            iconFile.set(virtualFile);
        }
        iconLabel.setIcon(icon);
        if (ObjectUtils.isNotEmpty(track)) {
            trackLabel.setIcon(new ImageIcon(track));
        } else {
            trackLabel.setIcon(Icons.INSTANCE.getTRACK());
        }
        this.useDefaultIcon.setSelected(useDefaultIcon);
        this.useDefaultTrack.setSelected(useDefaultTrack);
        addListener(icon, track);
        addTips();
        addComponentValidator();
    }

    private void addListener(Icon icon, BufferedImage track) {
        chooseBg.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                VirtualFile[] virtualFiles = FileChooser.chooseFiles(
                        new SingleImageChooserDescriptor(), null, null);
                if (virtualFiles.length > 0) {
                    trackFile.set(virtualFiles[0]);
                    trackLabel.setIcon(new ImageIcon(virtualFiles[0].getPath()));
                    useDefaultTrack.setSelected(false);
                }
            }
        });
        chooseIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                VirtualFile[] virtualFiles = FileChooser.chooseFiles(
                        new SingleImageChooserDescriptor(), null, null);
                if (virtualFiles.length > 0) {
                    iconFile.set(virtualFiles[0]);
                    iconLabel.setIcon(Icons.INSTANCE.cropAndResizeIcon(virtualFiles[0].getPath()));
                    useDefaultIcon.setSelected(false);
                }
            }
        });
        useDefaultIcon.addActionListener(l -> {
            if (useDefaultIcon.isSelected()) {
                iconLabel.setIcon(Icons.INSTANCE.getPANDA());
            } else {
                iconLabel.setIcon(icon);
            }
        });
        useDefaultTrack.addActionListener(l -> {
            if (useDefaultTrack.isSelected() || track == null) {
                trackLabel.setIcon(Icons.INSTANCE.getTRACK());
            } else {
                trackLabel.setIcon(new ImageIcon(track));
            }
        });
    }

    private void addTips() {
        JPanel iconTip = UI.PanelFactory.grid().splitColumns().add(
                UI.PanelFactory.panel(iconLabel).withComment(ProgressBarBundle.INSTANCE.message("icon.tip"))).createPanel();
        iconTipPane.add(iconTip, BorderLayout.CENTER);
        JPanel trackTip = UI.PanelFactory.grid().splitColumns().add(
                UI.PanelFactory.panel(trackLabel).withComment(ProgressBarBundle.INSTANCE.message("track.tip"))).createPanel();
        trackTipPane.add(trackTip, BorderLayout.CENTER);
        JPanel horizontalFlipTip = UI.PanelFactory.grid().splitColumns().add(
                UI.PanelFactory.panel(horizontalFlip).withComment(ProgressBarBundle.INSTANCE.message("horizontal.flip.tip"))
                        .moveCommentRight()).createPanel();
        horizontalFlipTipPane.add(horizontalFlipTip, BorderLayout.CENTER);
    }

    private void addComponentValidator() {
        new ComponentValidator(ProjectManager.getInstance().getDefaultProject()).withValidator(() -> {
            if (useDefaultTrack.isSelected()) {
                return null;
            }
            if (ObjectUtils.isNotEmpty(trackFile.get())
                    && Objects.equals(trackFile.get().getExtension(), "gif")) {
                return new ValidationInfo(ProgressBarBundle.INSTANCE.message("choose.static.image"), chooseBg);
            }
            return null;
        }).installOn(chooseBg);
    }
}
