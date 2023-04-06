package pers.wjx.plugin;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ui.UI;
import org.apache.commons.lang3.ObjectUtils;
import pers.wjx.plugin.progress.common.Icons;
import pers.wjx.plugin.progress.common.ProgressBarBundle;
import pers.wjx.plugin.progress.state.ProgressBarSettingState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    public ProgressBarConfigForm() {
        initComponent();
    }

    public JPanel getPanel() {
        return rootPane;
    }

    /**
     * 配置是否更改
     */
    public boolean configChanged() {
        ProgressBarSettingState setting = ProgressBarSettingState.Companion.getInstance();
        boolean modified = useDefaultIcon.isSelected() != setting.getUseDefaultIcon();
        modified |= useDefaultTrack.isSelected() != setting.getUseDefaultTrack();
        modified |= horizontalFlip.isSelected() != setting.getHorizontalFlip();
        modified |= ObjectUtils.isEmpty(trackFile.get()) && ObjectUtils.isNotEmpty(setting.getTrackFilePath());
        modified |= ObjectUtils.isNotEmpty(trackFile.get())
                && ObjectUtils.notEqual(trackFile.get().getPath(), setting.getTrackFilePath());

        modified |= ObjectUtils.isNotEmpty(iconFile.get())
                && ObjectUtils.notEqual(iconFile.get().getPath(), setting.getIconFilePath());
        return modified;
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

    public Icon getTrack() {
        return trackLabel.getIcon();
    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        ProgressBarSettingState setting = ProgressBarSettingState.Companion.getInstance();
        if (ObjectUtils.isNotEmpty(setting.getTrackFilePath())) {
            VirtualFile virtualFile = VirtualFileManager.getInstance().refreshAndFindFileByNioPath(Path.of(setting.getTrackFilePath()));
            trackFile.set(virtualFile);
        }
        if (ObjectUtils.isNotEmpty(setting.getIconFilePath())) {
            VirtualFile virtualFile = VirtualFileManager.getInstance().refreshAndFindFileByNioPath(Path.of(setting.getIconFilePath()));
            iconFile.set(virtualFile);
        }
        iconLabel.setIcon(setting.getIcon());
        if (ObjectUtils.isNotEmpty(setting.getTrack())) {
            trackLabel.setIcon(new ImageIcon(setting.getTrack()));
        } else {
            trackLabel.setIcon(Icons.INSTANCE.getTRACK());
        }
        useDefaultIcon.setSelected(setting.getUseDefaultIcon());
        useDefaultTrack.setSelected(setting.getUseDefaultTrack());
        addListener(setting);
        addTips();
        addComponentValidator();
    }

    private void addListener(ProgressBarSettingState setting) {
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
                iconLabel.setIcon(setting.getIcon());
            }
        });
        useDefaultTrack.addActionListener(l -> {
            if (useDefaultTrack.isSelected() || setting.getTrack() == null) {
                trackLabel.setIcon(Icons.INSTANCE.getTRACK());
            } else {
                trackLabel.setIcon(new ImageIcon(setting.getTrack()));
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
