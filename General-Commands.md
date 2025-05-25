# General Commands and TroubleShooting

## Compiling .java files:

### Set Hadoop classpath variable to compile java files - 
```
echo 'export HADOOP_CLASSPATH=$(hadoop classpath)' >> ~/.bashrc
source ~/.bashrc
```

### Compile:
```
javac -classpath $HADOOP_CLASSPATH -d /path/to/save/class_files /path/to/.java
```

### Create Jar:
```
jar -cvf jarfilename.jar -C /path/to/your/class_files .
```

### Run Jar:
```
hadoop jar jarfilename.jar jarMainFunctionName /hdfs_input/file /hdfs_output/file
```

### View Output:
```
hdfs dfs -cat /user/root/temp_output/part-*
```

### Upload from browser to HDFS: 
```
hdfs dfs -chmod 777 /
```

### Set up SSH:
```
ssh-keygen -t rsa -P "" -f ~/.ssh/id_rsa
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

#### check if ssh set up: 
```
ssh localhost
```

