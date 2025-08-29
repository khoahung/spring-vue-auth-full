
# Full demo projects: auth-service, user-service, fe-vue

# environment require

+ maven
+ java JDK 23
+ docker

## How to run

edit file .env for vueJS fix IP local in .env file 

+ change IP from row line 

VITE_API_BASE=http://192.168.1.101:8082

VITE_AUTH_BASE=http://192.168.1.101:8081

edit file application.yml both user-service and auth-service

+ edit line by IP server 
	
	url: http://192.168.1.101

Deploy in docker

./clean_docker.cmd

./build_docker.cmd

open FE http://192.168.1.101:5173

Account Admin default : admin/123456

