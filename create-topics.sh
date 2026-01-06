#!/bin/bash
export PATH=$PATH:/usr/bin

KAFKA_BROKER="localhost:9092"

create_topic() {
  TOPIC=$1
  PARTITIONS=$2
  REPLICATION=$3

  /usr/bin/kafka-topics \
    --bootstrap-server $KAFKA_BROKER \
    --create \
    --if-not-exists \
    --topic $TOPIC \
    --partitions $PARTITIONS \
    --replication-factor $REPLICATION
}

create_topic logs.album-service 3 1
create_topic logs.artist-service 3 1
create_topic logs.playlist-service 3 1
create_topic logs.listener-service 3 1
create_topic logs.song-service 3 1

echo "Successfully created topics:"
/usr/bin/kafka-topics --bootstrap-server $KAFKA_BROKER --list