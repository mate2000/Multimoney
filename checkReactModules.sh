echo "*** Checking node version ***"
if (which node > /dev/null)
    then
        echo "node already installed, version: $(node -v)"
else
  echo "installing node..."
  # if the OS is windows, install node from winget
  if [[ "$OSTYPE" == "msys" ]]
    then
      winget install -e --id OpenJS.NodeJS --accept-package-agreements --accept-source-agreements
  else
    # for linux or mac
    brew install npm
  fi
fi
echo "*** Checking yarn version ***"
if (which yarn > /dev/null)
    then
        echo "yarn already installed, version: $(yarn -v)"
else
  echo "installing yarn..."
  npm install --global yarn
fi
echo "*** Checking react node_modules ***"
if [ -d "./react_native_dependencies/node_modules" ]
    then
        echo "react node_modules ready!"
else
  echo "node_modules not present, running yarn to get them..."
  (cd react_native_dependencies && yarn install)
fi
exit