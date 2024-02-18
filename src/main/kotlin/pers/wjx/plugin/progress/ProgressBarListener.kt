package pers.wjx.plugin.progress

import com.intellij.ide.plugins.DynamicPluginListener
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.wm.IdeFrame
import pers.wjx.plugin.ProgressBarUI
import javax.swing.UIManager

/**
 * @author wjx
 */
class ProgressBarListener : LafManagerListener, DynamicPluginListener, ApplicationActivationListener {
    private lateinit var pluginId: PluginId
    init {
        updateProgressBarUI()
    }

    override fun lookAndFeelChanged(lafManager: LafManager) {
        updateProgressBarUI()
    }

    override fun pluginLoaded(pluginDescriptor: IdeaPluginDescriptor) {
        if (pluginId == pluginDescriptor.pluginId) {
            updateProgressBarUI()
        }
    }

    override fun applicationActivated(ideFrame: IdeFrame) {
        updateProgressBarUI()
    }

    companion object {
        private fun updateProgressBarUI() {
            UIManager.put("ProgressBarUI", ProgressBarUI::class.java.name)
            UIManager.getDefaults()[ProgressBarUI::class.java.name] = ProgressBarUI::class.java
        }
    }
}
