package pers.wjx.plugin.progress.state

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
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
)
class ProgressBarSettingState : PersistentStateComponent<ProgressBarSettingState> {
    var horizontalFlip: Boolean = true
    var useDefaultIcon: Boolean = true
    var useDefaultTrack: Boolean = true

    @OptionTag(converter = ImageIconInfoConverter::class)
    private var iconInfo: ImageIconInfo? = ImageIconInfo(null, Icons.PANDA, Icons.PANDA_LEFT)

    @OptionTag(converter = BufferedImageConverter::class)
    private var trackInfo: BufferedImageInfo? = BufferedImageInfo(null, null)

    fun getIcon(): Icon {
        return if (useDefaultIcon || getIconInfo().path == null || getIconInfo().imageIcon == null) {
            Icons.PANDA
        } else {
            getIconInfo().imageIcon!!
        }
    }

    fun getHorizontal(): Icon {
        return if (useDefaultIcon
            || getIconInfo().path == null
            || getIconInfo().imageIcon == null
            || getIconInfo().horizontalIcon == null
        ) {
            Icons.PANDA_LEFT
        } else {
            getIconInfo().horizontalIcon!!
        }
    }

    fun getTrack(): BufferedImage? {
        return if (!useDefaultTrack && getTrackInfo().path != null) {
            return getTrackInfo().bufferedImage
        } else {
            null
        }
    }

    fun getIconInfo(): ImageIconInfo {
        if (iconInfo == null) {
            iconInfo = ImageIconInfo(null, Icons.PANDA, Icons.PANDA_LEFT)
        }
        return iconInfo!!
    }

    fun setIconInfo(ic: ImageIconInfo) {
        iconInfo = ic
    }

    fun getTrackInfo(): BufferedImageInfo {
        if (trackInfo == null) {
            trackInfo = BufferedImageInfo(null, null)
        }
        return trackInfo!!
    }

    fun setTrackInfo(tc: BufferedImageInfo) {
        trackInfo = tc
    }

    companion object {
        @JvmStatic
        fun getInstance(): ProgressBarSettingState {
            return ApplicationManager.getApplication().getService(ProgressBarSettingState::class.java)
        }
    }

    override fun getState(): ProgressBarSettingState {
        if (iconInfo == null) {
            iconInfo = ImageIconInfo(null, Icons.PANDA, Icons.PANDA_LEFT);
        }
        if (trackInfo == null) {
            trackInfo = BufferedImageInfo(null, null);
        }
        return this
    }

    override fun loadState(state: ProgressBarSettingState) {
        return XmlSerializerUtil.copyBean(state, this)
    }

}