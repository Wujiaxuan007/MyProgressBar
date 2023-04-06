@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package pers.wjx.plugin.progress.common

import com.intellij.ui.components.JBLabel
import com.intellij.ui.scale.ScaleContext
import com.intellij.ui.scale.ScaleType
import com.intellij.util.IconUtil
import com.intellij.util.ui.ImageUtil
import pers.wjx.plugin.progress.ProgressBarConfigurable
import java.awt.Image
import java.awt.MediaTracker
import java.awt.Toolkit
import javax.swing.Icon
import javax.swing.ImageIcon


/**
 * @author wjx
 */
object Icons {
    var PANDA = ImageIcon(this.javaClass.getResource("/images/panda_right.gif"))
    var PANDA_LEFT = ImageIcon(this.javaClass.getResource("/images/panda_left.gif"))
    var TRACK = ImageIcon(this.javaClass.getResource("/images/track.png"))

    fun cropAndResizeIcon(path: String): Icon {
        val image = loadImage(path)
        val icon = ImageIcon(image)
        var minRectangle = ImageTool.getMinRectangle(
            ImageUtil.toBufferedImage(image)
        )
        // try again, gif 第一次 getMinRectangle 会出现 image 是全透明的情况
        if (minRectangle.height == 0) {
            minRectangle = ImageTool.getMinRectangle(ImageUtil.toBufferedImage(image))
        }
        val cropIcon = IconUtil.cropIcon(icon, minRectangle)
        return resizeIcon(cropIcon)
    }

    private fun resizeIcon(icon: Icon): Icon {
        var result = icon
        var scale = ProgressBarConfigurable.PIXEL / icon.iconHeight.toDouble()
        do {
            result = IconUtil.scale(result, ScaleContext.create(ScaleType.OBJ_SCALE.of(scale)))
            scale /= 0.1
        } while (scale < 0.1)
        if (scale < 1) {
            result = IconUtil.scale(result, ScaleContext.create(ScaleType.OBJ_SCALE.of(scale)))
        }
        return result
    }

    private fun loadImage(path: String): Image {
        val image = Toolkit.getDefaultToolkit().createImage(path)
        waitForImage(image)
        return image
    }

    private fun waitForImage(image: Image?): Boolean {
        if (image == null) return false
        val c = JBLabel()
        val track = MediaTracker(c)
        track.addImage(image, 0)
        try {
            track.waitForID(0)
        } catch (_: InterruptedException) {
        }
        return !track.isErrorID(0)
    }
}