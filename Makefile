DEFAULT_GOAL := create

pre: 
	@kubectl apply -f https://raw.githubusercontent.com/metallb/metallb/v0.13.7/config/manifests/metallb-native.yaml
	@kubectl wait --namespace metallb-system \
		--for=condition=ready pod \
		--selector=app=metallb \
		--timeout=120s
	@kubectl apply -f Manifestos/metallb-pool.yaml
	@kubectl apply -f Manifestos/setup.hosts.yaml

helm:
	@helmfile apply

pos:
	@kubectl apply -f Manifestos/harbor-credentials.yaml
	@kubectl apply -f Manifestos/pvc-jenkins-pipeline.yaml

create:
	@kind create cluster --config config.yaml

up: create pre helm pos

destroy:
	@kind delete clusters kind

passwd:
	@echo ""
	@echo "JENKINS: "
	@kubectl get secret -n jenkins jenkins -ojson | jq -r '.data."jenkins-admin-password"' | base64 -d
	@echo ""
	@echo ""
	@echo "GITEA:"
	@echo "r8sA8CPHD9!bt6d"
	@echo ""
	@echo "SONARQUBE:"
	@echo "WRA4eze1wpr9mfz-qyd"
	@echo ""
	@echo "ARGOCD: "
	@kubectl get secret -n argocd argocd-initial-admin-secret -ojson | jq -r '.data.password' | base64 -d
	@echo ""
	@echo ""
	@echo "JENKINS GITEA SERVICE ACCOUNT:"
	@echo "aqd.ufd8MQF2zna7azq"
	@echo ""
	@echo "HARBOR: "
	@echo "Harbor12345"
