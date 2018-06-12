#!/bin/bash

set -e

# First argument is the path to the .patch file

PATCH_PATH=$1

git apply --stat $PATCH_PATH &&
git apply --check $PATCH_PATH &&
git am --signoff < $PATCH_PATH
