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

create_topic artist.created 3 1
create_topic artist.updated 3 1
create_topic artist.deleted 3 1

create_topic song.created 3 1
create_topic song.updated 3 1
create_topic song.deleted 3 1
create_topic song.liked 3 1
create_topic song.unliked 3 1

create_topic album.created 3 1
create_topic album.updated 3 1
create_topic album.deleted 3 1

create_topic user.created 3 1
create_topic user.login 3 1
create_topic user.updated 3 1
create_topic user.deleted 3 1

create_topic playlist.created 3 1
create_topic playlist.updated 3 1
create_topic playlist.deleted 3 1
create_topic playlist.song.added 3 1
create_topic playlist.song.remeoved 3 1

echo "Successfully created topics:"
/usr/bin/kafka-topics --bootstrap-server $KAFKA_BROKER --list