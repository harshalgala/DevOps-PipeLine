# DevOps_MileStone2

### Members
   - Ami Sanghvi (asangha)
   - Harshal Gala (hgala2)
   - Harshal Gurjar (hkgurjar)
   - Payal Chheda (pchheda)

### Screencast URL
Screencast part 1: https://youtu.be/GbEEdMtd7Lo  <br/>
Screencast part 2: https://youtu.be/SZf4hPjKohk

### Useless Tests per build graph
![uselesstestssofar](https://media.github.ncsu.edu/user/5748/files/a1bdd284-c612-11e7-992a-e9f88e5fa731)

### Analysis
Link to the Analysis Report : https://github.ncsu.edu/asangha/DevOps_MileStone2/blob/master/Report.md

<br>Functions with BigO Violations : 0 </br>
<br>Functions with Sync Calls Violations  : 1 </br>
<br>Functions with Maximum Message Length Violations : 2 </br>
<br>Functions with Long Method Violations : 1 </br>

Details of the violations(Function and File Name) can be seen in the report link given above.

### Useless Tests Detected Report 
Link to the Useless Tests Detected Report : https://github.ncsu.edu/asangha/DevOps_MileStone2/blob/master/UselessTests.txt
<br/> We found **321** useless tests after running **100** builds

### Test suites & coverage reports
![testsuite](https://media.github.ncsu.edu/user/5748/files/41bfd9c4-b8fa-11e7-84de-e38196fb71c9)
<br/><br/><br/>
![coverage](https://media.github.ncsu.edu/user/5748/files/f8667346-b8f9-11e7-90b1-955083e17c08)


### Contribution
   - *Ami Sanghavi* - Analysis and build failure
   - *Payal Chheda* - Test suites, coverage, and test results, helped in Commit Fuzzer
   - *Harshal Gurjar* - Uselesss test detector
   - *Harshal Gala* - Commit Fuzzer
   - *End to End integration done by each member*

### Overall Setup Steps
   - Using vagrant, we created a new VM called trusty with private IP: 192.168.33.xx. This VM is a Ubuntu 14.04 machine that will serve as our configuration server with ansible. Next, we bring it up with vagrant
   - Using vagrant, we created another VM called xenial with private IP: 192.168.33.xx. This VM is a Ubuntu 16.04 machine that will serve as our jenkins server. Next, we bring it up with vagrant
   - As in workshop exercise, we set up ssh keys such that trusty can ssh into xenial server box
   - ssh into trusty and execute next steps
   - run 'sudo apt-get update'
   - Install git (sudo apt-get install git)
   - Install ansible (using 3 steps mentioned on https://github.com/CSC-DevOps/CM/blob/master/Ansible.md)
   - run 'git clone https://github.ncsu.edu/asangha/DevOps_MileStone2.git'
   - run ansible-playbook "jenkins.yml"
Sample command: ansible-playbook DevOps_Milestone2/jenkins.yml -i inventory
   - On running the script, you will be prompted for NCSU github username and password credentials (needed to clone itrust). Enter those.
   - Let the script run to success
   - Now visit the ip of the xenial machine (our jenkins server) and check jobs have been built

#### Methodologies/Mechanisms used:
   - We are using git post commit hook for triggering builds of fuzzer branch
   - For running Test Suites we are running mvn test(We increased max_connections)
   - For running test coverages we use jacoco plugin
   - For Commit Fuzzer and useless Test Detector we created Java applications.
   - For Analysis of checkbox.io we used NodeJs
     
#### Issues faced:
   - mvn test was failing (not enough poolable connections exception). We resolved this by increasing max_connections value in /etc/mysql/my.cnf
   - Overall integration
   - NOTE: For running 100 builds, we had trouble of vagrant jenkins server machine running out of memory. So we used AWS EC2 instance as our jenkins server
   
### Purpose of each file 
   - *README.md* - Project Milestone 2 Report. It highlights setup steps, issues faced and everything else relevant and important to the project
   - *Report.md* - Checkboxio static analysis report markdown file  
   - *jenkins.yml* - Ansible playbook file used to set up jenkins server and its dependencies. It includes tasks from setup_itrust.yml
   - *setup_itrust.yml* - downloads itrust dependencies i.e.the MySQL server, creates itrust build job, itrust fuzzer build job, downloads and copies fuzzer and useless test detector jar from git repo and builds itrust job.
   - *setup_checkbox.yml* - creates checkboxio build job, copies analysis nodejs project and triggers checkboxio build
   - *Run_Jars.yml* - triggers the fuzzer & useless test detector for itrust
   - *itrust_config.xml* - config.xml used to create main build job with post build action for iTrust project that runs Run_Fuzzer.yml ansible playbook(triggers fuzzer)
   - *itrust_fuzzer_config.xml* - jenkins build config file for running build, test suite, coverage on fuzzed commit on "fuzzer" branch
   - *checkboxio_config.xml* - jenkins build config file for running build & analysis on checkboxio
   - *create-credentials.groovy* - groovy file used to create github username password credentials in the jenkins server needed to clone iTrust
   - "Fuzzer/" - Folder with maven + java project for Commit Fuzzer for itrust
   - "UselessTestDetector/" - Folder with maven + java project for useless test detector for itrust
   - "Analysis/" - Folder with nodejs project for static analysis on checkboxio
   - *UselessTests.txt* - Useless Tests detected report
   
### Repositories used
   - For ITrust: https://github.ncsu.edu/pchheda/iTrust-v23
   - For build-tools (just cloning jars): https://github.com/payalchheda/build-tools
