package pers.wjx.plugin.progress.common

import java.awt.Rectangle
import java.awt.image.BufferedImage


/**
 * @author wjx
 */
object ImageTool {

    fun getMinRectangle(image: BufferedImage): Rectangle {
        val raster = image.alphaRaster ?: return Rectangle(0, 0, image.width, image.height)
        val width = raster.width
        val height = raster.height
        var left = 0
        var top = 0
        var right = width - 1
        var bottom = height - 1
        var minRight = width - 1
        var minBottom = height - 1
        top@ while (top <= bottom) {
            for (x in 0 until width) {
                if (raster.getSample(x, top, 0) != 0) {
                    minRight = x
                    minBottom = top
                    break@top
                }
            }
            top++
        }
        if (minRight == width - 1 && minBottom == height - 1) {
            // 全透明
            return Rectangle(0, 0, 0, 0);
        }
        left@ while (left < minRight) {
            for (y in height - 1 downTo top + 1) {
                if (raster.getSample(left, y, 0) != 0) {
                    minBottom = y
                    break@left
                }
            }
            left++
        }
        bottom@ while (bottom > minBottom) {
            for (x in width - 1 downTo left) {
                if (raster.getSample(x, bottom, 0) != 0) {
                    minRight = x
                    break@bottom
                }
            }
            bottom--
        }
        right@ while (right > minRight) {
            for (y in bottom downTo top) {
                if (raster.getSample(right, y, 0) != 0) {
                    break@right
                }
            }
            right--
        }
        if (left - 1 > 0) {
            left--
        }
        if (top - 1 > 0) {
            top--
        }
        var w = right - left + 2
        if (w + 1 < width - 1) {
            w++
        }
        var h = bottom - top + 2
        if (h + 1 < height - 1) {
            h++
        }
        return Rectangle(left, top, w, h)
    }
}


