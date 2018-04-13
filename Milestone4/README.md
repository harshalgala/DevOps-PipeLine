# DevOps_Milestone4_Special
### Members
   - Ami Sanghvi (asangha)
   - Harshal Gala (hgala2)
   - Harshal Gurjar (hkgurjar)
   - Payal Chheda (pchheda)

### Contribution
   - *Ami Sanghavi & Payal Chheda (pair programming)*- Setup of Haproxy, Setup Servers and Auto Deployment with scaling handling
   - *Harshal Gurjar* - LoadPoller and Report
   - *Harshal Gala* - MonitoringTool and Report

### Video Link  

[Presentation](https://onedrive.live.com/?authkey=%21AOamQkPf2Vd4XRk&cid=A15ACD826A363CCD&id=A15ACD826A363CCD%21531&parId=A15ACD826A363CCD%21530&o=OneUp)
<br/> Note: Onedrive may present a popup asking you to sign in. Just click cross (X) button and skip ahead to the video.

### Special Milestone

We have implemented a modified version of Doctor Monkey known as Surgeon Monkey we have kept a check on the CPU utilizaition of the instances which are polled by the loadpoller which is running on the load balancer. 
Here, in the start three instances are created and deployed along with iTrust on it successfully and then in their http://ip:3000/health we can see the CPU utilization. We have set the threshold for the experimentation case to be lower as 20% and upper as 50%. 
If the threshold crosses 50% loadpoller polls a new instances and deploys iTrust on it which then divides the load and the process is carried on as usual. When the threshold of a particular instance goes below the threshold then that particular instances is removed from polling. We have kept the polling to be in every three minutes (this can be modified as per the use required) 
We have used *stress* command to manipulate the CPU utilizaion and get the results.

The complete scenario can be seen in this illustration below:


![Alt Special Milestone](https://github.ncsu.edu/pchheda/DevOps_Milestone4_Special/blob/master/SurgeonMonkey.gif)

### Overall Setup Steps 
 - You should have ansible,boto,boto3 and the environment variables of AWS credentials set on the loadbalancer from where the load poller will poll instances
 - The scripts should be executed it this fashion as follows 
 1) provision-instances-itrust.yml 
 2) setup_servers.yml
 3) setup_haproxy.yml
 4) monitor.yml
 5) load.yml
 Here the scale and scaledown scripts are executed in the backgroud by the loadbalancer which is ran by the load.yml file and this is how the surgeon monkey in a very nice way not only handles the CPU load but gives you the deployed version when load above threshold unlike autoscaling where only an instance is been created and nothing more.

### Screenshots
https://github.ncsu.edu/pchheda/DevOps_Milestone4_Special/tree/master/Screenshots

#### Methodologies/Mechanisms used 
 - We used ansible to write the scripts to create AWS instnces and deploy the iTrust project on it. 
 - We used nodejs and expressjs to create the monitoring tool and the loadpoller 
 
### Issues faced: 
 - Automating the complete process 
 - Running Ansible scripts from nodejs script with call back funtions 
 - Automatically updating the inventory file as and when new instances created and destroyed
 
 ### Purpose of Each File 

*setup_servers.yml* - This file deploys iTrust on the EC2 instances which were created by the *provision-instances-itrsut.yml*  
*setup_haproxy.yml* - This file sets up the haproxy on the machine which acts as the loadbalancer and balances the load of all the instances which are polled. 

*provision-instances-itrust.yml* - This is the script which when executed creates the initail three instances of EC2. 

*monitor.yml* - This is the script which is ran on the instances which are up as it loads the monitoring tool and sends the CPU utilization of that particular instance to the load balancer. 

*scale.yaml* - This script is executed if the threshold is above the threshold limit and then creates an EC2 instance along with iTrust deployed on it. 

*scaledown.yaml* - This executes when some instance is used below the lower bound threshold then it removes the ec2 instance from loadpolling 

*load.yml* - It clones the loadpoller and install all the dependencies and runs it on the machine where loadbalancer is running 

*templates* - It is the set of files for haproxy which is required such as haproxy.cfg and others 

*LoadPoller* - It is the tool which pools the EC2 instances and is shown in the link below 

*MonitoringTool* - It is the tool which monitors and sends the CPU utilization to the loadpoller 

[LoadPoller](https://github.com/harshalgala/Loadpoller) 

[Monitoring Tool](https://github.com/harshalgala/MonitoringTool)
 
 ### Surgeon Monkey
![Alt Surgeon Monkey](https://github.ncsu.edu/pchheda/DevOps_Milestone4_Special/blob/master/SurgeonMonkey.png)
