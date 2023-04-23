#!/bin/bash
set -e

# ensure current folder is in the modulemate root
cd "${BASH_SOURCE%/*}"

echo "▶️  Pulling…"
git pull

echo "▶️  Building…"
./build.sh

echo ""
echo "✅ Updated to latest version."
