package pers.wjx.plugin.progress.error

import com.intellij.ide.BrowserUtil
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.text.StringUtil
import com.intellij.util.ArrayUtil
import com.intellij.util.Consumer
import pers.wjx.plugin.progress.common.ProgressBarBundle
import java.awt.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * @author wjx
 */
class ErrorSubmitter : ErrorReportSubmitter() {
    private val url = "https://github.com/Wujiaxuan007/YourProgressBar/issues/new?"
    private val pluginId = "pers.wjx.plugin.yourProgressBar"
    private val label = "exception"
    private val stacktraceLen = 6500

    override fun getReportActionText(): String {
        return ProgressBarBundle.message("report.to.vendor")
    }

    override fun submit(
        events: Array<out IdeaLoggingEvent>,
        additionalInfo: String?,
        parentComponent: Component,
        consumer: Consumer<in SubmittedReportInfo>
    ): Boolean {
        val event = ArrayUtil.getFirstElement(events)
        var title = "Exception: "
        var stacktrace = "Please paste the full stacktrace from the IDEA error popup.\n"
        if (event != null) {
            val throwableText = event.throwableText
            val exceptionTitle: String = throwableText.lines().first()
            title += if (!StringUtil.isEmptyOrSpaces(exceptionTitle)) exceptionTitle else "<Fill in title>"
            if (!StringUtil.isEmptyOrSpaces(throwableText)) {
                val quotes = "\n```\n"
                stacktrace += quotes + StringUtil.first(
                    throwableText,
                    stacktraceLen,
                    true
                ) + quotes
            }
        }
        val plugin = PluginManagerCore.getPlugin(PluginId.getId(pluginId))
        val pluginVersion = if (plugin != null) plugin.version else ""
        val ideaVersion = ApplicationInfo.getInstance().build.asString()
        val template = StringBuilder()
        template.append("### Description\n")
        if (additionalInfo != null) {
            template.append(additionalInfo).append("\n")
        }
        template.append("\n")
        template.append("### Stacktrace\n").append(stacktrace).append("\n")
        template.append("### Version and Environment Details\n")
            .append("Operation system: ").append(SystemInfo.getOsNameAndVersion()).append("\n")
            .append("IDE version: ").append(ideaVersion).append("\n")
            .append("Plugin version: ").append(pluginVersion).append("\n")
        val charset = StandardCharsets.UTF_8
        val url = String.format(
            "%stitle=%s&labels=%s&body=%s",
            url,
            URLEncoder.encode(title, charset),
            URLEncoder.encode(label, charset),
            URLEncoder.encode(template.toString(), charset)
        )
        BrowserUtil.browse(url)
        consumer.consume(
            SubmittedReportInfo(
                null,
                "GitHub issue",
                SubmittedReportInfo.SubmissionStatus.NEW_ISSUE
            )
        )
        return true
    }
}