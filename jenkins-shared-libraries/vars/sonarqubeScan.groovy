def call (body) {

  def settings = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = settings
  body()

  container('sonar-scanner-cli') {
    sh '''
    sonar-scanner -X \
      -Dsonar.projectKey=${JOB_NAME%/*}-${GIT_BRANCH} \
      -Dsonar.login=${SONAR_LOGIN} \
      -Dsonar.qualitygate.wait=true \
      -Dqualitygate.wait=true
    '''
  }

}
