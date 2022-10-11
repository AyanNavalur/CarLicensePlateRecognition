# CarTextRecognition

CS 643-851
Author - Ayan Navalur

AWS project to build an image recognition pipeline in AWS, using two EC2 instances, S3, SQS, and Rekognition.
Images are fetched from stored S3 Objects and image labelling is done using AWS Rekognition. AWS SQS service is used for communication between the two EC2 instances.

--EC2 Setup
Click on "Services" -> "EC2"
Click on "Launch Instances"
Enter a name for the instance in the "Name and Tags" section
Next select "Amazon Linux 2 Kernel 5.10 AMI 2.0.20220912.1 x86_64 HVM gp2" in the "Application and OS Images (Amazon Machine Image)" section
Select the "t2.micro type (Free tier eligible)" in the "Instance type" section
In "Key pair (login)" section select "Create a new key pair" to create a new key for the first time. When creating the second instance select the existing key pair from the dropdown
In "Network settings" section check "Create security group" to create a new security group. Check all 3 options which allow SSH, HTTPS and HTTP traffic. In the SSH option select "MyIP" from the dropdown
Leave the default values for "Configure Storage" and "Advanced details" sections and click on "Launch Instance" button to create a new instance
Similarly create another instance with the same key pair and security group

--Run the following command in the terminal to set the correct permissions for the .pem file:
chmod 400 key_pair.pem

--Connecting to running EC2 instances
Convert the key pair from .pem file to .ppk file using PuttyGen
SSH to the instances using Putty to connect to the EC2 instances. Enter the address as ec2-user@<YOUR_EC2_INSTANCE_PUBLIC_IPV4_ADDRESS> which is the "Public IPv4 DNS" attribute of either EC2 instance and load the .ppk file in the Connection->SSH->Auth

--Installing Coretto on the EC2 instance
Run the following commands to install Amazon Coretto
$ sudo amazon-linux-extras enable corretto8
$ sudo yum clean metadata
$ sudo yum install -y java-1.8.0-amazon-corretto
After installation check using the below command to confirm Java is installed
$ java -version

--Accessing Credentials for Java SDK

<!-- fill this -->

--Copying Credentials to EC2 instances
Create property file
$mkdir .aws
$touch .aws/credentials
Vim and copy credentials to property file
$vi .aws/credentials
\*\*Note: Crendentials change after a session expires. You would need to copy the new credentials again.

--Creating SQS
Click on "Services" -> "Simple Queue Service"
Click on "Create Queue"
Check "FIFO"
Enter name for the queue
Leave the other configurations to default and click on "Create Queue"

--Creating jar files
Clone the repository into your local machine
git clone https://github.com/AyanNavalur/CarTextRecognition.git
% create jar files

--Deploying jar files to EC2 instances
You can copy the files using FTP command or FTP clients like WinSCP
Place both the jar files in the respective EC2 instances
% run command

\*\*Note: All above instructions are for Windows OS. Few intructions might change for Linux/Mac systems.
