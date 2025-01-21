package pers.wjx.plugin;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import pers.wjx.plugin.progress.common.ProgressBarBundle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wjx
 */
public class SingleImageChooserDescriptor extends FileChooserDescriptor {
    private static final List<String> supportedExtensions = Arrays.asList(ImageIO.getReaderFormatNames());

    private final List<ImageIcon> icons = new ArrayList<>(2);

    public SingleImageChooserDescriptor() {
        super(true, false, false, false, false, false);
    }

    @Override
    public String getTitle() {
        return ProgressBarBundle.INSTANCE.message("choose.image");
    }

    @Override
    public void validateSelectedFiles(VirtualFile @NotNull [] files) throws Exception {
        for (VirtualFile file : files) {
            if (file != null && !supportedExtensions.contains(file.getExtension())) {
                throw new Exception(ProgressBarBundle.INSTANCE.message("please.choose.image.file"));
            }
        }
    }
}
