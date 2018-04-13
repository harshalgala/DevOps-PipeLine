# Milestone1

### Members
   - Ami Sanghvi (asangha)
   - Harshal Gala (hgala2)
   - Harshal Gurjar (hkgurjar)
   - Payal Chedda (pchheda)

### Screencast URL
https://youtu.be/ulnfHCfNgNw 

### Contribution
   - *Ami Sanghavi & Payal Chheda (pair programming)*- Setup Jenkins, Create & Build jobs (for both checkbox.io and iTrust), setup basic post build task that triggers ansible script, provision AWS instance, end to end Integration
   - *Harshal Gurjar* - checkbox.io setup & end to end Integration
   - *Harshal Gala* - iTrust setup & end to end integration

### Overall Setup Steps
   - Using vagrant, we created a new VM called trusty with private IP: 192.168.33.xx. This VM is a Ubuntu 14.04 machine that will serve as our configuration server with ansible. Next, we bring it up with vagrant
   - Using vagrant, we created another VM called xenial with private IP: 192.168.33.xx. This VM is a Ubuntu 16.04 machine that will serve as our jenkins server. Next, we bring it up with vagrant
   - As in workshop exercise, we set up ssh keys such that trusty can ssh into xenial server box
   - ssh into trusty and execute next steps
   - run 'sudo apt-get update'
   - Install git (sudo apt-get install git)
   - Install ansible (using 3 steps mentioned on https://github.com/CSC-DevOps/CM/blob/master/Ansible.md)
   - run 'git clone https://github.ncsu.edu/pchheda/Milestone1.git'
   - run ansible-playbook "jenkins.yml" to configure Jenkins server with build jobs and post build actions for itrust and checkbox.io
Sample command: ansible-playbook -i inventory Milestone1/jenkins.yml
   - On running the script, you will be prompted for NCSU github username and password credentials (needed to clone itrust). Enter those.
   - You will be prompted for aws_access_key and aws_secret_key. This is needed to provision VMs using AWS to set up postbuild actions in jenkins. Enter those.
   - Let the script run to success
   - Now visit the ip of the xenial machine (our jenkins server) and check jobs have been built
   - Using the log console output for each of the jobs, figure the IP (Say - 11.11.11.11 for checkboxio and  12.12.12.12 for itrust)
   - Type in http://ip:8080/iTrust/ to check iTrust (in this case - http://12.12.12.12:8080/iTrust/) 

#### Methodologies/Mechanisms used:
  - For Jenkins, we have disabled setup wizard and security
  - We used Jenkins cli jar to create & build jobs using config.xml for each of the 2 projects
  - We used create-credentials.groovy to create ncsu github username and password credentials. This helps to clone itrust during its build. Credentials created have id 'pass'
  - We used cred-aws.groovy to create AWS access key and secret key credentials. This is needed in the postbuild actions to provision new VMs
  - For each project, we have ansible scripts for the post-build actions. These scripts begin with provisioning the VM using AWS, creating an in memory host file with host group ec2hosts and then set up python, and the projects on the provisioned VM

#### Issues faced:
 - We tried multiple approaches to create Jenkins user credentials. Although some worked, we had trouble with building the job using jenkins cli jar. Building the job using the cli jar needed anonymous user to have read permissions. Hence, finally, we concluded by disabling security in jenkins completely

 - Disabling jenkins security was a task in its own. If you look up online, it simply says that a variable(useSecurity) in a config file needs to be changed from true to false. However, everytime we did this and restarted the service, it automatically reset back to true. To get rid of this, we first disabled the set up wizard and next disabled security by making that flag from true to false

 - iTrust is github repository on NCSU accessible only to NCSU users. We needed to automate the git clone for the project without hardcoding/compromising/checking in any NCSU credentials. We used vars_prompt to take in these credentials from the user and used a groovy script to create Github credentials on jenkins

 - Handling credentials for AWS was difficult. We didn't want to check-in any credentials. We used vars_prompt to take in aws_access_key and aws_secret_key. We then installed jenkins plugin "aws-credentials" and used groovy script to create the aws credentials in jenkins with id 'aws'. Because, those credentials are now readily available to jenkins, it is able to provision EC2 instances.

 - For checkboxio, one of the trickiest parts was setting up environment variables. Also, it took time for us to figure out that in the file, /etc/nginx/sites-available/default , we needed to point root to newly created public_html folder

 - Integrating part 1, 2 and 3 was the toughest. When trying to run the entire setup on vagrant, sometimes, the machine would run out of memory (usually because of iTrust). Also, simply integrating the scripts to work together, to ensure the remote_user was fine etc. was tough

### Purpose of each file 
   - *Readme.md* - Project Milestone 1 Report. It highlights setup steps, issues faced and everything else relevant and important to the project
   - *jenkins.yml* - Ansible playbook file used to set up jenkins server and its dependencies. It also contains tasks to create jobs and build them
   - *checkbox-io-build-config.xml* - config.xml used to create build job with post build action for checkbox.io project
   - *itrust-build-config.xml* - config.xml used to create build job with post build action for iTrust project
   - *create-credentials.groovy* - groovy file used to create github username password credentials in the jenkins server needed to clone iTrust
   - *cred-aws.groovy* - groovy file used to create aws_access_key and aws_secret_key credentials in jenkins server needed during post build action to provision VM
   - *aws-provision.yml* - ansible playbook file executed during postbuild action of checkboxio. It provisions a VM and installs checkbox.io on it
   - *itrust_aws.yml* - ansible playbook file executed during postbuild action of iTrust. It provisions a VM and installs iTrust on it
   
### Itrust 

#### Setup Steps:
   - Created a directory named code where the whole itrust-v23 project will get set.
   - Install all the software required i.e Java, Apache Tomcat, MySQL, MySQL server and client, Maven
   - Cloned the git repository from [here](https://github.ncsu.edu/engr-csc326-staff/iTrust-v23.git)
   - We created a war file to run it using Apache tomcat and we can access it at http://ip:8080/iTrust-23.0.0/

#### Issues faced:
   - The problem of MySQL server faced as (Unmet Dependencies) was solved by restarting the MySQL service
   - Giving privileges to 'root' to access the MySQL database solved writing SQl queries of granting privileges, update mysql.users, and then Flushed privileges.
   - Restarting the mysql services were an important part before maven package as well as any changes in the mysql database and this was solved by restarting the services.

### CheckBox.io

### Steps in Ansible Script to setup checkbox.io

1.  Install required packages : NodeJs, Python 2, nginx, npm. Dotenv
2.  Set Key for Mongo packages and Add mogo packages repo
3.  Clone checkbox repository
4.  Install mongodb and start Server
5.  Create admin user for mongo
6. Set Environment variables
7. Add ip and default start page to nginx config file
8. Copy nginx.conf file from checkbox repo to nginx folder
9. Copy default configuration file from checkbox repo to nginx folder
10. Restart nginx
11. Enable secure authentication for MongoDB and Restart Mongodb
12. Install forever to start Nodejs
13. Install nodejs dependencies
14. Start sample nodejs app using forever

### Tips:
- Make sure to setup environment variables for MongoDB
- Update nginx.conf file to include ip address
- Update default.conf to include default start page

