
# Kafka Deployment

Create Kafka cluster based on 3 nodes. 
Each node will have a Kafka broker and Zookeeper node.

## Ansible Setup

Create an Ansible Docker image to run the Ansible playbooks to deploy Zookeeper and Kafka etc..

```
# Use ubuntu as base image
docker run -it -v ${pwd}/ansible_home:/export ubuntu /bin/bash

# Ansible Deployment - https://docs.ansible.com/ansible/latest/installation_guide/intro_installation.html#installing-ansible-on-ubuntu

sudo apt update
sudo apt install software-properties-common
sudo add-apt-repository --yes --update ppa:ansible/ansible
sudo apt install ansible

# Use sshpass to manage SSH connectivity and key exchange.
sudo apt install -y sshpass
```

## Run Ansible playbook

Precondition: 
* Requires the SSH connectivity to be completed successfully i.e. key exchanged.
* Define the "hosts" file to identify the 3 nodes

```
# Sample "hosts" file
[brokers]
broker.1 ansible_host={node1_ipaddress}
broker.2 ansible_host={node2_ipaddress}
broker.3 ansible_host={node3_ipaddress}
```

Run the playbooks.
```
# Connect to the Ansible docker instance.
docker ps -a
docker start <container_id> # Start container if it is no running
docker exec -it <container_id> /bin/bash
cd /export

ansible-playbook -i hosts zookeeper-dependencies.yml
ansible-playbook -i hosts kafka-dependencies.yml
```

TODO: Auto-startup of zookeeper and kafka

## Start Zookeeper Cluster (ensemble)

Start zookeeper
```
cd /opt/apache-zookeeper-*/bin
./zkServer.sh start
```

Check zookeeper status

```
./zkServer.sh status

/usr/bin/java
ZooKeeper JMX enabled by default
Using config: /opt/apache-zookeeper-3.7.0-bin/bin/../conf/zoo.cfg
Client port found: 2181. Client address: localhost. Client SSL: false.
Mode: standalone
```

## Start Kafka Cluster

Start kafka
```
kafka-server-start -daemon /etc/server.properties
```

Check Kafka cluster participants are registered in Zookeeper.

```
cd /opt/apache-zookeeper-*/bin
./zkCli.sh

get /zookeeper/config
server.1=w.x.y.z:2888:3888:participant
server.2=w.x.y.z:2888:3888:participant
server.3=w.x.y.z:2888:3888:participant
```

# Partitions and Replication Factors

## Two partition One replication factor
```
kafka-topics --create --zookeeper localhost:2181 --partitions 2 --topic topic-2p1r --replication-factor 1

kafka-topics --zookeeper localhost:2181 --describe --topic topic-2p1r
Topic: topic-2p1r	PartitionCount: 2	ReplicationFactor: 1	Configs:
	Topic: topic-2p1r	Partition: 0	Leader: 2	Replicas: 2	Isr: 2
	Topic: topic-2p1r	Partition: 1	Leader: 3	Replicas: 3	Isr: 3

```

```
zkCli.sh
ls /brokers/topics/topic-2p1r/partitions
[0, 1]

get /brokers/topics/topic-2p1r/partitions/0/state
{"controller_epoch":1,"leader":2,"version":1,"leader_epoch":0,"isr":[2]}

get /brokers/topics/topic-2p1r/partitions/1/state
{"controller_epoch":1,"leader":3,"version":1,"leader_epoch":0,"isr":[3]}
```

## Two partition Two replication factor
```
kafka-topics --create --zookeeper localhost:2181 --partitions 2 --topic topic-2p2r --replication-factor 2

kafka-topics --zookeeper localhost:2181 --describe --topic topic-2p2r
Topic: topic-2p2r	PartitionCount: 2	ReplicationFactor: 2	Configs:
	Topic: topic-2p2r	Partition: 0	Leader: 2	Replicas: 2,1	Isr: 2,1
	Topic: topic-2p2r	Partition: 1	Leader: 3	Replicas: 3,2	Isr: 3,2
```

### Write to topic

```
kafka-console-producer --bootstrap-server w.x.y.z:9092 --topic topic-2p2r
>message_1
>message_2
>message_3

```

Kafka decides which partition to write for a particular topic
```
kafka-run-class kafka.tools.DumpLogSegments --files topic-2p2r-0/00000000000000000000.log --print-data-log
Dumping topic-2p2r-0/00000000000000000000.log
Starting offset: 0
baseOffset: 0 lastOffset: 0 count: 1 baseSequence: -1 lastSequence: -1 producerId: -1 producerEpoch: -1 partitionLeaderEpoch: 0 isTransactional: false isControl: false position: 0 CreateTime: 1623375966901 size: 77 magic: 2 compresscodec: NONE crc: 4058765786 isvalid: true
| offset: 0 CreateTime: 1623375966901 keysize: -1 valuesize: 9 sequence: -1 headerKeys: [] payload: message_3

kafka-run-class kafka.tools.DumpLogSegments --files topic-2p2r-1/00000000000000000000.log --print-data-log
Dumping topic-2p2r-1/00000000000000000000.log
Starting offset: 0
baseOffset: 0 lastOffset: 0 count: 1 baseSequence: -1 lastSequence: -1 producerId: -1 producerEpoch: -1 partitionLeaderEpoch: 0 isTransactional: false isControl: false position: 0 CreateTime: 1623374406869 size: 77 magic: 2 compresscodec: NONE crc: 415224712 isvalid: true
| offset: 0 CreateTime: 1623374406869 keysize: -1 valuesize: 9 sequence: -1 headerKeys: [] payload: message_1
baseOffset: 1 lastOffset: 1 count: 1 baseSequence: -1 lastSequence: -1 producerId: -1 producerEpoch: -1 partitionLeaderEpoch: 0 isTransactional: false isControl: false position: 77 CreateTime: 1623375941799 size: 77 magic: 2 compresscodec: NONE crc: 3325065883 isvalid: true
| offset: 1 CreateTime: 1623375941799 keysize: -1 valuesize: 9 sequence: -1 headerKeys: [] payload: message_2

```

### Read from topic

```
kafka-console-consumer --bootstrap-server w.x.y.z:9092 --topic topic-2p2r --from-beginning
message_3
message_1
message_2

```

## Group consumers

```
kafka-console-consumer --bootstrap-server w.x.y.z:9092 --topic topic-2p2r --group app_1

kafka-consumer-groups --bootstrap-server w.x.y.z:9092 --describe --all-groups

Consumer group 'app_1' has no active members.

GROUP           TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
app_1           topic-2p2r      0          2               2               0               -               -               -
app_1           topic-2p2r      1          4               4               0               -               -               -

```

### Adjust partitions to accomodate more consumers

Consumer assignment is baed on `partition.assignment.strategy`.
By default it is RangeAssignor so the number of "effective consumer" is bounded by the number of "partition".
If the number of consumer exceeds the partition then the extra consumers will be idle.

```
kafka-topics --zookeeper localhost:2181  --topic topic-2p2r --alter --partitions 3
WARNING: If partitions are increased for a topic that has a key, the partition logic or ordering of the messages will be affected
Adding partitions succeeded!

kafka-topics --zookeeper localhost:2181  --topic topic-2p2r --describe
Topic: topic-2p2r	PartitionCount: 3	ReplicationFactor: 2	Configs:
	Topic: topic-2p2r	Partition: 0	Leader: 2	Replicas: 2,1	Isr: 2,1
	Topic: topic-2p2r	Partition: 1	Leader: 3	Replicas: 3,2	Isr: 3,2
	Topic: topic-2p2r	Partition: 2	Leader: 1	Replicas: 1,3	Isr: 1,3
```

Adding new partition enable additional consumers to process messages.
```
kafka-consumer-groups --bootstrap-server w.x.y.z:9092 --all-groups --describe

GROUP           TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                           HOST            CLIENT-ID
app_1           topic-2p2r      2          3               3               0               consumer-app_1-1-c1043554-27e0-477a-a4ef-0e3eebe2d4c5 /w.x.y.z
app_1           topic-2p2r      1          11              11              0               consumer-app_1-1-6c48b81a-19ab-4a2f-a5f6-087663446f2f /w.x.y.z
app_1           topic-2p2r      0          8               8               0               consumer-app_1-1-0f63c6bc-5930-4321-ba7f-d4edc025cc88 /w.x.y.z
```

# Miscellaneous

## Dump log

```
pwd
/var/lib/kafka/topic-2p2r-1
root@cd93dc51471c:/var/lib/kafka/topic-2p2r-1# kafka-run-class kafka.tools.DumpLogSegments --files 00000000000000000000.log --print-data-log
Dumping 00000000000000000000.log
Starting offset: 0
baseOffset: 0 lastOffset: 0 count: 1 baseSequence: -1 lastSequence: -1 producerId: -1 producerEpoch: -1 partitionLeaderEpoch: 0 isTransactional: false isControl: false position: 0 CreateTime: 1623374406869 size: 77 magic: 2 compresscodec: NONE crc: 415224712 isvalid: true
| offset: 0 CreateTime: 1623374406869 keysize: -1 valuesize: 9 sequence: -1 headerKeys: [] payload: message_1
```

## SSH Tunnelling

Add virtual IP address

```
sudo ifconfig lo0 alias 127.0.0.2 up
sudo ifconfig lo0 alias 127.0.0.3 up
sudo ifconfig lo0 alias 127.0.0.4 up
```

Add kafka advertised listener to the hosts file

```
# /etc/hosts
127.0.0.2 ${kafka_broker1}
127.0.0.3 ${kafka_broker2}
127.0.0.4 ${kafka_broker3}

```

Add SSH tunnel between local env to the remote kafka brokers.

Approach 1:

```
ssh -L ${local_host}:${local_port}:${remote_local_host}:${remote_local_port} ${ssh_username}@${host1_public_ip}
```

Approach 2:

```
# ~/.ssh/config
Host kafka${n}
  Hostname ${host1_public_ip}
  User ${ssh_username}
  LocalForward ${local_host}:${local_port} ${remote_local_host}:${remote_local_port}
```

# Errors

## Replication factor: 1 larger than available brokers: 0.

```
kafka-topics --zookeeper localhost:2181/kafka --create --topic test --replication-factor 1 --partitions 3
Error while executing topic command : Replication factor: 1 larger than available brokers: 0.
[2021-06-10 01:14:13,495] ERROR org.apache.kafka.common.errors.InvalidReplicationFactorException: Replication factor: 1 larger than available brokers: 0.
 (kafka.admin.TopicCommand$)
```

Make sure Kafka server is running and Zookeeper shows it.

```
kafka-server-start /etc/kafka/server.properties

zkCli.sh
[zk: localhost:2181(CONNECTED) 0] ls /
[admin, brokers, cluster, config, consumers, controller, controller_epoch, feature, isr_change_notification, latest_producer_id_block, log_dir_event_notification, zookeeper]
[zk: localhost:2181(CONNECTED) 1] ls /brokers
[ids, seqid, topics]
[zk: localhost:2181(CONNECTED) 2] ls /brokers/ids
[0]
[zk: localhost:2181(CONNECTED) 3] ls /brokers/ids/0
[]
[zk: localhost:2181(CONNECTED) 4] get /brokers/ids/0
{"features":{},"listener_security_protocol_map":{"PLAINTEXT":"PLAINTEXT"},"endpoints":["PLAINTEXT://localhost.localdomain:9092"],"jmx_port":-1,"port":9092,"host":"localhost.localdomain","version":5,"timestamp":"1623288007386"}
[zk: localhost:2181(CONNECTED) 5] qui
```

## The size of the current ISR Set(3) is insufficient to satisfy the min.isr requirement of 2 for partition

Kafka cluster setup is comprised of 3 Kafka brokers.
The following config specifies the minimum insync replicas to be 3, which is not possible because there are only 3 brokers and therefore only 2 replicas hence the error.
```
kafka-topics --bootstrap-server localhost:9092 --create --topic topic-1p3r --partitions 1 --replication-factor 3 --config min.insync.replicas=3
```
