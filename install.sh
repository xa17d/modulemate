#!/bin/bash
set -e

# ensure current folder is in the modulemate root
cd "${BASH_SOURCE%/*}"

echo "▶️  Building…"
mkdir -p build
./gradlew jar

echo ""
echo "▶️  Creating m script…"

mScript=$(cat <<EndOfMessage
#!/bin/bash
java -jar $PWD/modulemate/build/libs/modulemate.jar \$@
EndOfMessage
)
echo "$mScript" > build/m

chmod +x build/m

echo ""
echo "▶️  Installing to /usr/local/bin/m…"

sudo cp -n "build/m" "/usr/local/bin/m" ||
    (echo '⚠️  Installation to `/usr/local/bin/m` failed.' &&
     echo 'You can just take the `m` file from `build/m` and copy it to any folder that is added to $PATH, to manually install modulemate.' &&
     echo '' &&
     echo 'Possible reasons for failure:' &&
     echo '- Permission not granted' &&
     echo '- /usr/local/bin/m already exists' &&
     exit 1)

echo ""
echo '✅ Successfully installed modulemate! You can now use `m` in your terminal!'
