# Hadoop Installation

[Reference Link](https://arjunkrish.medium.com/step-by-step-guide-to-setting-up-hadoop-on-ubuntu-installation-and-configuration-walkthrough-60e493e9370d)

## Step 1: Install Java Development Kit:
```
sudo apt update && sudo apt install openjdk-8-jdk
```

## Step 2: Verify Java Version
```
java -version
```

## Step 3:  Install SSH:
```
sudo apt install ssh
```

## Step 4:  Create the Hadoop User:
```
sudo adduser hadoop
```

## Step 5: Switch User 
```
su - hadoop
```

## Step 6: Configure SSH
```
ssh-keygen -t rsa
```

## Step 7: Set permissions
```
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 640 ~/.ssh/authorized_keys
```

## Step 8: SSH to the localhost
```
ssh localhost
```

## Step 9: Install Hadoop
```
wget https://dlcdn.apache.org/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz
```

```
tar -xvzf hadoop-3.3.6.tar.gz
mv hadoop-3.3.6 hadoop
```

## Step 10: Set up environment variables for Java and Hadoop in your system

```
nano ~/.bashrc
```
##### Append the following lines to the file
```
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export HADOOP_HOME=/home/hadoop/hadoop
export HADOOP_INSTALL=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export HADOOP_YARN_HOME=$HADOOP_HOME
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
export PATH=$PATH:$HADOOP_HOME/sbin:$HADOOP_HOME/bin
export HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib/native"
export HADOOP_CLASSPATH=$(hadoop classpath)
```

##### Load the above configuration into the current environment
```
source ~/.bashrc
```

##### Additionally, you should configure the ‘JAVA_HOME’ in the ‘hadoop-env.sh’ file
```
nano $HADOOP_HOME/etc/hadoop/hadoop-env.sh
```

##### Search for the 'JAVA_HOME' line and configure it
```
JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

## Step 11: Configuring Hadoop

##### Create the name node and data node directories 
```
cd hadoop/
mkdir -p ~/hadoopdata/hdfs/{namenode,datanode}
```

##### Edit the ‘core-site.xml’ file and replace the name with your system hostname
```
nano $HADOOP_HOME/etc/hadoop/core-site.xml
```

###### Change the ‘fs.defaultFS’ property value according to your system hostname (keep it same as below)
```
<configuration>
<property>
<name>fs.defaultFS</name>
<value>hdfs://localhost:9000</value>
</property>
</configuration>
```

##### Save and close the file. Then, edit the ‘hdfs-site.xml’
```
nano $HADOOP_HOME/etc/hadoop/hdfs-site.xml
```

```
<configuration>
<property>
<name>dfs.replication</name>
<value>1</value>
</property>
<property>
<name>dfs.namenode.name.dir</name>
<value>file:///home/hadoop/hadoopdata/hdfs/namenode</value>
</property>
<property>
<name>dfs.datanode.data.dir</name>
<value>file:///home/hadoop/hadoopdata/hdfs/datanode</value>
</property>
</configuration>
```

##### Next, edit the ‘mapred-site.xml’ file
```
nano $HADOOP_HOME/etc/hadoop/mapred-site.xml
```

```
<configuration>
<property>
<name>yarn.app.mapreduce.am.env</name>
<value>HADOOP_MAPRED_HOME=$HADOOP_HOME/home/hadoop/hadoop/bin/hadoop</value>
</property>
<property>
<name>mapreduce.map.env</name>
<value>HADOOP_MAPRED_HOME=$HADOOP_HOME/home/hadoop/hadoop/bin/hadoop</value>
</property>
<property>
<name>mapreduce.reduce.env</name>
<value>HADOOP_MAPRED_HOME=$HADOOP_HOME/home/hadoop/hadoop/bin/hadoop</value>
</property>
</configuration>
```

##### Finally, edit the ‘yarn-site.xml’ file
```
nano $HADOOP_HOME/etc/hadoop/yarn-site.xml
```

```
<configuration>
<property>
<name>yarn.nodemanager.aux-services</name>
<value>mapreduce_shuffle</value>
</property>
</configuration>
```

## Step 12: Start the Hadoop Cluster

##### Before starting the Hadoop cluster, you must format the Namenode as the ‘hadoop’ user.

```
hdfs namenode -format
```

##### Start the Hadoop cluster
```
start-all.sh
```

##### Check the status of all Hadoop services
```
jps
```

## Step 13: Access Hadoop Namenode and Resource Manager

```
sudo apt install net-tools
ifconfig
```

##### Access the Namenode
```
http://your-server-ip:9870
```

##### Access the Resource Manager
```
http://your-server-ip:8088
```

## Step 14: Verify the Hadoop Cluster

```
hdfs dfs -mkdir /test1
hdfs dfs -mkdir /logs
```

```
hdfs dfs -ls /
```

```
hdfs dfs -put /var/log/* /logs/
```

Go to the web interface, click on the Utilities => Browse the file system. You should see your directories,

## Step 15: To stop Hadoop services
```
stop-all.sh
```