#!/bin/bash
set -e

# ensure current folder is in the modulemate root
cd "${BASH_SOURCE%/*}"

echo "▶️  Pulling…"
git pull

echo "▶️  Building…"
./build.sh

echo "▶️  Update user configuration"
if [ -d ~/.modulemate/.git ]; then
    echo "Updating ~/.modulemate/..."
    cd ~/.modulemate/
    git pull
else
    echo "Cannot update ~/.modulemate, because it's not a git repository"
fi

echo ""
echo "✅ Updated to latest version."
