/**
 * Shared library step: dockerBuildPush
 * Usage: dockerBuildPush(registry: '...', repo: '...', tag: '...', credId: 'aws-credentials')
 */
def call(Map config) {
    def registry = config.registry
    def repo     = config.repo
    def tag      = config.tag ?: 'latest'
    def credId   = config.credId ?: 'aws-credentials'
    def region   = config.region ?: 'us-east-1'

    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: credId]]) {
        sh """
            aws ecr get-login-password --region ${region} | \
            docker login --username AWS --password-stdin ${registry}

            docker build -t ${registry}/${repo}:${tag} .
            docker tag  ${registry}/${repo}:${tag} ${registry}/${repo}:latest
            docker push ${registry}/${repo}:${tag}
            docker push ${registry}/${repo}:latest

            echo "Pushed ${registry}/${repo}:${tag}"
        """
    }
}
