#!/usr/bin/sh

# allow for cpu arch emulation
docker run --privileged --rm tonistiigi/binfmt --install all
