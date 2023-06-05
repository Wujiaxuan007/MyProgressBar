package pers.wjx.plugin.progress.common

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.project.Project
import com.intellij.util.DisposeAwareRunnable

/**
 * @author wjx
 */
object InvokeUtils {
    fun invokeLater(p: Project?, r: Runnable) {
        if (isNoBackgroundMode()) {
            r.run()
        } else {
            ApplicationManager.getApplication().invokeLater(
                DisposeAwareRunnable.create(r, p),
                ModalityState.defaultModalityState()
            )
        }
    }

    fun isNoBackgroundMode(): Boolean {
        return ApplicationManager.getApplication().isUnitTestMode || ApplicationManager.getApplication().isHeadlessEnvironment
    }

}