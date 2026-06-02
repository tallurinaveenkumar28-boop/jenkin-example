/**
 * Shared library step: trivyScan
 * Usage: trivyScan(image: 'myrepo/myapp:tag', severity: 'HIGH,CRITICAL', failOnVuln: false)
 */
def call(Map config) {
    def image      = config.image      ?: error('image is required')
    def severity   = config.severity   ?: 'HIGH,CRITICAL'
    def exitCode   = config.failOnVuln ? '1' : '0'

    sh """
        docker run --rm \
            -v /var/run/docker.sock:/var/run/docker.sock \
            aquasec/trivy:latest image \
            --exit-code ${exitCode} \
            --severity ${severity} \
            --format table \
            ${image}
    """
}
