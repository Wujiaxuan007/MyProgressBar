package pers.wjx.plugin.progress

import com.intellij.openapi.application.ApplicationManager
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
        fun getInstance(): ProgressBarConfigurable {
            return ApplicationManager.getApplication().getService(ProgressBarConfigurable::class.java)
        }
    }

    override fun createComponent(): JComponent? {
        settingForm = ProgressBarConfigForm()
        return settingForm!!.panel
    }

    override fun isModified(): Boolean {
        if (settingForm == null) {
            settingForm = ProgressBarConfigForm()
        }
        return settingForm!!.configChanged()
    }

    override fun apply() {
        val settings: ProgressBarSettingState = ProgressBarSettingState.getInstance()
        settings.useDefaultIcon = settingForm!!.useDefaultIcon
        settings.useDefaultTrack = settingForm!!.useDefaultTrack
        settings.horizontalFlip = settingForm!!.horizontalFlip
        if (!ObjectUtils.isEmpty(settingForm!!.iconFile.get())
            && ObjectUtils.notEqual(settingForm!!.iconFile.get().path, settings.iconFilePath)
        ) {
            try {
                settings.iconFilePath = settingForm!!.iconFile.get().path
                settings.iconInfo = ImageIconInfo(settings.iconFilePath!!, settingForm!!.icon)
            } catch (_: Exception) {
            }
        }
        if (!ObjectUtils.isEmpty(settingForm!!.trackFile.get())
            && ObjectUtils.notEqual(settingForm!!.trackFile.get().path, settings.trackFilePath)
        ) {
            try {
                settings.trackFilePath = settingForm!!.trackFile.get().path
                settings.trackInfo = BufferedImageInfo(
                    settings.trackFilePath!!,
                    IconUtil.toBufferedImage(settingForm!!.track)
                )
            } catch (_: Exception) {
            }
        }
    }

    override fun reset() {
        val settings: ProgressBarSettingState = ProgressBarSettingState.getInstance()
        settingForm?.useDefaultIcon = settings.useDefaultIcon
        settingForm?.useDefaultTrack = settings.useDefaultTrack
        settingForm?.horizontalFlip = settings.horizontalFlip

        if (ObjectUtils.isNotEmpty(settings.trackFilePath)) {
            val virtualFile =
                VirtualFileManager.getInstance().refreshAndFindFileByNioPath(Path.of(settings.trackFilePath!!))
            settingForm?.trackFile!!.set(virtualFile)
        }
        if (ObjectUtils.isNotEmpty(settings.iconFilePath)) {
            val virtualFile =
                VirtualFileManager.getInstance().refreshAndFindFileByNioPath(Path.of(settings.iconFilePath!!))
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