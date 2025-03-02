#!/bin/bash


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

