/**
 * Shared library step: notifySlack
 * Usage: notifySlack('SUCCESS', 'good', 'Optional extra message')
 */
def call(String status, String color, String extra = '') {
    def jobName    = env.JOB_NAME
    def buildNum   = env.BUILD_NUMBER
    def buildUrl   = env.BUILD_URL
    def branch     = env.BRANCH_NAME ?: 'N/A'
    def commit     = env.GIT_COMMIT?.take(7) ?: 'N/A'
    def duration   = currentBuild.durationString

    def msg = """\
*${status}* — `${jobName}` #${buildNum}
Branch: `${branch}` | Commit: `${commit}` | Duration: ${duration}
${extra ? '\n' + extra : ''}
<${buildUrl}|View Build>"""

    slackSend(color: color, message: msg)
}
