/**
 * Shared library step: helmDeploy
 * Usage: helmDeploy(appName: '...', chartPath: '...', imageRepo: '...', imageTag: '...', environment: 'dev')
 */
def call(Map config) {
    def appName    = config.appName    ?: error('appName is required')
    def chartPath  = config.chartPath  ?: './helm/springboot-app'
    def imageRepo  = config.imageRepo  ?: error('imageRepo is required')
    def imageTag   = config.imageTag   ?: 'latest'
    def environment = config.environment ?: 'dev'
    def namespace  = config.namespace  ?: 'default'
    def dryRun     = config.dryRun     ?: false
    def cluster    = config.cluster    ?: 'springboot-eks-cluster'
    def region     = config.region     ?: 'us-east-1'

    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
        sh """
            aws eks update-kubeconfig --region ${region} --name ${cluster}

            helm upgrade --install ${appName} ${chartPath} \
                --namespace ${namespace} \
                --set image.repository=${imageRepo} \
                --set image.tag=${imageTag} \
                --set environment=${environment} \
                ${dryRun ? '--dry-run' : ''} \
                --wait --timeout 5m \
                --atomic

            kubectl rollout status deployment/${appName} -n ${namespace} --timeout=3m
        """
    }
}
