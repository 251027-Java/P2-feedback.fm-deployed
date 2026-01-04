#!/usr/bin/sh

tagName=$1

docker images --format '{{.Repository}}:{{.Tag}}:{{.ID}}' | grep "${tagName}" | cut -f 3 -d ':' | xargs docker rmi -f
docker builder prune -af
