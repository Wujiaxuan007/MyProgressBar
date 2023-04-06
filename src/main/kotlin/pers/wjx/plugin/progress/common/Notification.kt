package pers.wjx.plugin.progress.common

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

/**
 * @author wjx
 */
object Notification {
    fun showWarning(content: String, project: Project?) {
        Notification(
            ProgressBarBundle.message("your.progress.bar.display.name"),
            ProgressBarBundle.message("your.progress.bar.display.name"), content, NotificationType.WARNING
        ).notify(project)
    }
}