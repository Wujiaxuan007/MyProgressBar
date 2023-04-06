package pers.wjx.plugin.progress.state

import com.intellij.util.IconUtil
import com.intellij.util.xmlb.Converter
import pers.wjx.plugin.progress.common.Notification
import pers.wjx.plugin.progress.common.ProgressBarBundle
import java.io.File
import javax.swing.ImageIcon

/**
 * @author wjx
 */
class BufferedImageConverter : Converter<BufferedImageInfo>() {
    override fun toString(value: BufferedImageInfo): String {
        return value.path
    }

    override fun fromString(value: String): BufferedImageInfo {
        if (!File(value).exists()) {
            Notification.showWarning(ProgressBarBundle.message("img.cannot.found.use.default", value), null)
            return BufferedImageInfo(value, null)
        }
        val imageIcon = ImageIcon(value)
        return BufferedImageInfo(value, IconUtil.toBufferedImage(imageIcon))
    }
}