package com.example

class PipelineUtils implements Serializable {

    static String getImageTag(script) {
        def commit = script.sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
        def ts     = script.sh(script: 'date +%Y%m%d%H%M%S', returnStdout: true).trim()
        return "${ts}-${commit}"
    }

    static boolean isProdBranch(String branch) {
        return branch == 'main' || branch == 'master'
    }

    static void printBanner(script, String message) {
        script.echo "=" * 60
        script.echo " ${message}"
        script.echo "=" * 60
    }
}
