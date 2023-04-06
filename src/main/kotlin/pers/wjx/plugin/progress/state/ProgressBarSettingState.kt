package pers.wjx.plugin.progress.state

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.IconUtil
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.OptionTag
import pers.wjx.plugin.progress.common.Icons
import java.awt.image.BufferedImage
import javax.swing.Icon


/**
 * @author wjx
 */
@State(
    name = "ProgressBarSettingState",
    storages = [Storage("ProgressBarSettingState.xml")],
    category = SettingsCategory.PLUGINS
)
class ProgressBarSettingState : PersistentStateComponent<ProgressBarSettingState> {
    var trackFilePath: String? = null
    var iconFilePath: String? = null
    var horizontalFlip: Boolean = true
    var useDefaultIcon: Boolean = true
    var useDefaultTrack: Boolean = true

    @OptionTag(converter = ImageIconInfoConverter::class)
    var iconInfo: ImageIconInfo? = null

    @OptionTag(converter = BufferedImageConverter::class)
    var trackInfo: BufferedImageInfo? = null

    fun getIcon(): Icon {
        return if (useDefaultIcon || iconFilePath == null) {
            Icons.PANDA
        } else if (iconInfo == null || iconInfo!!.imageIcon == null) {
            Icons.PANDA
        } else {
            iconInfo!!.imageIcon!!
        }
    }

    fun getHorizontal(): Icon {
        return if (useDefaultIcon || iconInfo == null || iconInfo!!.imageIcon == null) {
            Icons.PANDA_LEFT
        } else {
            IconUtil.flip(iconInfo!!.imageIcon!!, true)
        }
    }

    fun getTrack(): BufferedImage? {
        return if (!useDefaultTrack && trackFilePath != null && trackInfo != null) {
            return trackInfo!!.bufferedImage
        } else {
            null
        }
    }

    companion object {
        fun getInstance(): ProgressBarSettingState {
            return ApplicationManager.getApplication().getService(ProgressBarSettingState::class.java)
        }
    }

    override fun getState(): ProgressBarSettingState {
        return this
    }

    override fun loadState(state: ProgressBarSettingState) {
        return XmlSerializerUtil.copyBean(state, this)
    }

}