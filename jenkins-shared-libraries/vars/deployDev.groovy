def call (body) {

  def settings = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = settings
  body()

  container('alpine') {
    sh '''
      apk add openssh git
      mkdir $HOME/.ssh
      cp $JENKINS_SSH_PRIVATE_KEY $HOME/.ssh/id_rsa
      chmod 400 $HOME/.ssh/id_rsa
      ssh-keyscan gitea.localhost.com > $HOME/.ssh/known_hosts
      rm -rf helm-applications
      git config --global user.name "Jenkins CI"
      git config --global user.email "jenkins@mateuslippime.me"
      
      if [ ! -d helm-applications ]; then
        git clone git@gitea.localhost.com:mateuslippime/helm-applications.git
      fi
      cd helm-applications/${JOB_NAME%/*}/
      IMAGE_TAG="$(cat /artifacts/dev.artifact)"
      sed -i -E "s/dev-[0-9a-z]{10}/${IMAGE_TAG}/g" values-dev.yaml
      git add values-dev.yaml
      git commit -m "[${JOB_NAME%/*}|dev] - deploy ${IMAGE_TAG}" --allow-empty
      git push
    '''
  }

}
