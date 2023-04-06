package pers.wjx.plugin.progress.state

import com.intellij.util.xmlb.Converter
import pers.wjx.plugin.progress.common.Icons
import pers.wjx.plugin.progress.common.Notification
import pers.wjx.plugin.progress.common.ProgressBarBundle
import java.io.File

/**
 * @author wjx
 */
class ImageIconInfoConverter : Converter<ImageIconInfo>() {
    override fun toString(value: ImageIconInfo): String {
        return value.path
    }

    override fun fromString(value: String): ImageIconInfo {
        if (!File(value).exists()) {
            Notification.showWarning(ProgressBarBundle.message("img.cannot.found.use.default", value), null)
            return ImageIconInfo(value, null)
        }
        val icon = Icons.cropAndResizeIcon(value)
        return ImageIconInfo(value, icon)
    }
}