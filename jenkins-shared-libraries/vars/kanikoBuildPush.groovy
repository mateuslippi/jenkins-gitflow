def call (body) {

  def settings = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = settings
  body()

  container('kaniko') {
    sh '''
      REPOSITORY="harbor.localhost.com/mateuslippime"
      ENVIRONMENT=""

      if [ $(echo $GIT_BRANCH | grep -E "^develop$") ]; then
        IMAGE_TAG="dev-${GIT_COMMIT:0:10}"
        ENVIRONMENT="dev"
      elif [ $(echo $GIT_BRANCH | grep -E "^hotfix-.*") ]; then
        IMAGE_TAG="${GIT_BRANCH#*-}-${GIT_COMMIT:0:10}"
        ENVIRONMENT="stg"
      fi

      DESTINATION=${REPOSITORY}/${JOB_NAME%/*}:${IMAGE_TAG}
      
      /kaniko/executor \
        --context $(pwd) \
        --destination ${DESTINATION} \
        --insecure

      echo "${IMAGE_TAG}" > /artifacts/${ENVIRONMENT}.artifact
    '''
  }

}
