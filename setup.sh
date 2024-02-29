#!/bin/sh
set -e

/usr/bin/mc --version

# Setup local host
/usr/bin/mc config host rm local
/usr/bin/mc config host add --quiet --api s3v4 local http://minio:9000 gif gifsecret

# Create Core API buckets
/usr/bin/mc mb --quiet local/kaamelott/
