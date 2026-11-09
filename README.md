# Forjo - Vulnerable web application

## Overall presentation of project architecture

- **backend** (Java spring backend, not exposed)
- **frontend** (Angular frontend, not exposed)
- **bot_xss** (Python script to simulate an admin who check XSS messages) (not working)
- **ftp** (Basic ftp server containing a confidential CSV for vulnerability exploitation)
- **reverse_proxy** (Nginx reverse proxy to resolve the frontend dns as http://forjo.training.thales)

## How to run

### **============= Requirements ============**

- docker
- docker-compose
- Internet access (docker hub, maven repository, npm registry...)


### **============= Production =============**
#### **Run**

```bash
$> docker-compose up -d --build
```

This command will build the needed containers, if it's not already done.

You will have to configure the /etc/hosts file to add the reverse proxy dns :
```bash
$> echo "127.0.0.1 forjo.training.thales" | sudo tee -a /etc/hosts > /dev/null
```

Once the containers are built and running, you can access to the application at this url : http://forjo.training.thales


**Hot reload**:
`Hot reload` is enable through dockers for frontend and backend !

## How to contribute to development - (Dockerized version)

Just run 

```bash
$> docker-compose -f docker-compose-dev.yml up -d --build
```

and code !
# forjo
# forjo
# forjo
