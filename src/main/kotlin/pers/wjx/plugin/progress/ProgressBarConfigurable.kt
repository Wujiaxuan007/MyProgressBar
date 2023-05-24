package pers.wjx.plugin.progress

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.IconUtil
import org.apache.commons.lang3.ObjectUtils
import pers.wjx.plugin.ProgressBarConfigForm
import pers.wjx.plugin.progress.common.ProgressBarBundle
import pers.wjx.plugin.progress.state.BufferedImageInfo
import pers.wjx.plugin.progress.state.ImageIconInfo
import pers.wjx.plugin.progress.state.ProgressBarSettingState
import java.nio.file.Path
import javax.swing.JComponent


/**
 * @author wjx
 */
class ProgressBarConfigurable : Configurable {
    private var settingForm: ProgressBarConfigForm? = null

    companion object {
        const val PIXEL = 20.0
    }

    override fun createComponent(): JComponent? {
        val settings: ProgressBarSettingState = ProgressBarSettingState.getInstance()
        settingForm = ProgressBarConfigForm(
            settings.iconInfo,
            settings.trackInfo,
            settings.getIcon(),
            settings.getTrack(),
            settings.useDefaultIcon,
            settings.useDefaultTrack
        )
        return settingForm!!.panel
    }

    override fun isModified(): Boolean {
        val setting = ProgressBarSettingState.getInstance()
        var modified = settingForm!!.useDefaultIcon != setting.useDefaultIcon
        modified = modified or (settingForm!!.useDefaultTrack != setting.useDefaultTrack)
        modified = modified or (settingForm!!.horizontalFlip != setting.horizontalFlip)
        modified =
            modified or (ObjectUtils.isEmpty(settingForm!!.trackFile.get()) && ObjectUtils.isNotEmpty(setting.trackInfo.path))
        modified = modified or (ObjectUtils.isNotEmpty(settingForm!!.trackFile.get())
                && ObjectUtils.notEqual(settingForm!!.trackFile.get().path, setting.trackInfo.path))

        modified = modified or (ObjectUtils.isNotEmpty(settingForm!!.iconFile.get())
                && ObjectUtils.notEqual(settingForm!!.iconFile.get().path, setting.iconInfo.path))
        return modified
    }

    override fun apply() {
        val settings: ProgressBarSettingState = ProgressBarSettingState.getInstance()
        settings.useDefaultIcon = settingForm!!.useDefaultIcon
        settings.useDefaultTrack = settingForm!!.useDefaultTrack
        settings.horizontalFlip = settingForm!!.horizontalFlip
        if (!ObjectUtils.isEmpty(settingForm!!.iconFile.get())
            && ObjectUtils.notEqual(settingForm!!.iconFile.get().path, settings.iconInfo.path)
        ) {
            settings.iconInfo =
                ImageIconInfo(settingForm!!.iconFile.get().path, settingForm!!.icon, settingForm!!.horizontalIcon)
        }
        if (!ObjectUtils.isEmpty(settingForm!!.trackFile.get())
            && ObjectUtils.notEqual(settingForm!!.trackFile.get().path, settings.trackInfo.path)
        ) {
            settings.trackInfo = BufferedImageInfo(
                settingForm!!.trackFile.get().path,
                IconUtil.toBufferedImage(settingForm!!.track)
            )
        }
    }

    override fun reset() {
        val settings: ProgressBarSettingState = ProgressBarSettingState.getInstance()
        settingForm?.useDefaultIcon = settings.useDefaultIcon
        settingForm?.useDefaultTrack = settings.useDefaultTrack
        settingForm?.horizontalFlip = settings.horizontalFlip

        if (ObjectUtils.isNotEmpty(settings.trackInfo.path)) {
            val virtualFile =
                VirtualFileManager.getInstance().refreshAndFindFileByNioPath(Path.of(settings.trackInfo.path!!))
            settingForm?.trackFile!!.set(virtualFile)
        }
        if (ObjectUtils.isNotEmpty(settings.iconInfo.path)) {
            val virtualFile =
                VirtualFileManager.getInstance().refreshAndFindFileByNioPath(Path.of(settings.iconInfo.path!!))
            settingForm?.iconFile!!.set(virtualFile)
        }
    }

    override fun getDisplayName(): String {
        return ProgressBarBundle.getMessage("your.progress.bar.display.name")
    }

    override fun disposeUIResources() {
        settingForm = null
    }
}