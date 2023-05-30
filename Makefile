tag=dev
app=scb-ast-otp-service
values=values.yaml
nameSpace=dev
registry=083888084688.dkr.ecr.ap-southeast-1.amazonaws.com

build:
	docker build --platform linux/amd64 -t ${registry}/${app}:${tag} .

push:
	aws ecr get-login-password --region ap-southeast-1 | docker login --username AWS --password-stdin 083888084688.dkr.ecr.ap-southeast-1.amazonaws.com
	docker push ${registry}/${app}:${tag}

deploy:
	kubectl apply -f ./k8s/dev/configmap.yml
	kubectl apply -f ./k8s/dev/secret.yml
	kubectl apply -f ./k8s/dev/deployment.yml
	kubectl apply -f ./k8s/dev/service.yml
delete:
	kubectl delete -f ./k8s/dev/configmap.yml
	kubectl delete -f ./k8s/dev/secret.yml
	kubectl delete -f ./k8s/dev/deployment.yml
	kubectl delete -f ./k8s/dev/service.yml
