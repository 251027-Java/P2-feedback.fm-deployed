#!/usr/bin/sh

tagName=$1

if [ -n "${tagName}" ]; then
    docker images --format '{{.Repository}}:{{.Tag}}:{{.ID}}' | grep "${tagName}" | cut -f 3 -d ':' | xargs docker rmi -f
fi

# https://docs.docker.com/reference/cli/docker/buildx/prune/
# keep caches
docker builder prune -af --filter "type!=exec.cachemount"
docker builder prune -af --filter "until=24h"
