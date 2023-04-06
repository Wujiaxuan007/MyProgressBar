package pers.wjx.plugin.progress

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import pers.wjx.plugin.ProgressBarUI
import javax.swing.UIManager

/**
 * @author wjx
 */
class ProgressBarLafManagerListener : LafManagerListener {
    init {
        updateProgressBarUI()
    }

    override fun lookAndFeelChanged(lafManager: LafManager) {
        updateProgressBarUI()
    }

    companion object {
        private fun updateProgressBarUI() {
            UIManager.put("ProgressBarUI", ProgressBarUI::class.java.name)
            UIManager.getDefaults()[ProgressBarUI::class.java.name] = ProgressBarUI::class.java
        }
    }
}
