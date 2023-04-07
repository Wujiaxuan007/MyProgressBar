package pers.wjx.plugin.progress.state

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
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
    var horizontalFlip: Boolean = true
    var useDefaultIcon: Boolean = true
    var useDefaultTrack: Boolean = true

    @OptionTag(converter = ImageIconInfoConverter::class)
    var iconInfo: ImageIconInfo = ImageIconInfo(null, Icons.PANDA, Icons.PANDA_LEFT)

    @OptionTag(converter = BufferedImageConverter::class)
    var trackInfo: BufferedImageInfo = BufferedImageInfo(null, null)

    fun getIcon(): Icon {
        return if (useDefaultIcon || iconInfo.path == null || iconInfo.imageIcon == null) {
            Icons.PANDA
        } else {
            iconInfo.imageIcon!!
        }
    }

    fun getHorizontal(): Icon {
        return if (useDefaultIcon
            || iconInfo.path == null
            || iconInfo.imageIcon == null
            || iconInfo.horizontalIcon == null
        ) {
            Icons.PANDA_LEFT
        } else {
            iconInfo.horizontalIcon!!
        }
    }

    fun getTrack(): BufferedImage? {
        return if (!useDefaultTrack && trackInfo.path != null) {
            return trackInfo.bufferedImage
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