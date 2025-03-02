#!/bin/bash

# Run this after keygen step and local ssh cmd is working

wget https://dlcdn.apache.org/hadoop/common/hadoop-3.3.6/hadoop-3.3.6.tar.gz

tar -xvzf hadoop-3.3.6.tar.gz
mv hadoop-3.3.6 hadoop

bashrcstr=$(cat <<EOF
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export HADOOP_HOME=/home/hadoop/hadoop
export HADOOP_INSTALL=\$HADOOP_HOME
export HADOOP_MAPRED_HOME=\$HADOOP_HOME
export HADOOP_COMMON_HOME=\$HADOOP_HOME
export HADOOP_HDFS_HOME=\$HADOOP_HOME
export HADOOP_YARN_HOME=\$HADOOP_HOME
export HADOOP_COMMON_LIB_NATIVE_DIR=\$HADOOP_HOME/lib/native
export PATH=\$PATH:\$HADOOP_HOME/sbin:\$HADOOP_HOME/bin
export HADOOP_OPTS="-Djava.library.path=\$HADOOP_HOME/lib/native"
EOF
)

echo "$bashrcstr" >> ~/.bashrc

source ~/.bashrc

echo 'export HADOOP_CLASSPATH=$(hadoop classpath)' >> ~/.bashrc

source ~/.bashrc

JAVA_HOME="JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64"

echo "$JAVA_HOME" >>  $HADOOP_HOME/etc/hadoop/hadoop-env.sh

mkdir -p ~/hadoopdata/hdfs/{namenode,datanode}


xmlconfigure=$(cat <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
EOF
)

xmlyarn=$(cat <<EOF
<?xml version="1.0"?>
EOF
)



coresite=$(cat <<EOF
<configuration>
<property>
<name>fs.defaultFS</name>
<value>hdfs://localhost:9000</value>
</property>
</configuration>
EOF
)

hdfssite=$(cat <<EOF
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
EOF
)

mapredsite=$(cat <<EOF
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
EOF
)

yarnsite=$(cat <<EOF
<configuration>
<property>
<name>yarn.nodemanager.aux-services</name>
<value>mapreduce_shuffle</value>
</property>
</configuration>
EOF
)

echo -e "$xmlconfigure\n$coresite" > $HADOOP_HOME/etc/hadoop/core-site.xml

echo -e "$xmlconfigure\n$hdfssite" > $HADOOP_HOME/etc/hadoop/hdfs-site.xml

echo  -e "$xmlconfigure\n$mapredsite" > $HADOOP_HOME/etc/hadoop/mapred-site.xml

echo -e "$xmlyarn\n$yarnsite" > $HADOOP_HOME/etc/hadoop/yarn-site.xml

hdfs namenode -format

echo "All done: run start-all.sh and jps now"
