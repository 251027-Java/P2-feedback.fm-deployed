#!/usr/bin/sh

tagName=$1

if [ -n "${tagName}" ]; then
    docker images --format '{{.Repository}}:{{.Tag}}:{{.ID}}' | grep "${tagName}" | cut -f 3 -d ':' | xargs docker rmi -f
fi

# docker builder prune -af
