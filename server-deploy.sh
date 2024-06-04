#!/bin/bash

# Variables
HOST="root@167.172.144.172"
PRIVATE_KEY="~/.ssh/id_ed25519"
FRONTEND_PATH="./frontend"
BACKEND_PATH="./backend"
BUILD_SCRIPT="./build-docker.sh"
ANDROID_BUILD_SCRIPT="./build-android.sh"

DOCKER_COMPOSE_DIRECTORY="/root/Desktop/docker"
DOCKER_HUB_USER="thealanmc"
FRONTEND_IMAGE="etn-frontend"
BACKEND_IMAGE="etn-backend"

# build the docker image

echo "####################"
echo "Deploying the application"
echo "####################"

echo "Fetching current versions from the remote docker-compose file..."
# ssh -i "$PRIVATE_KEY" "$HOST" <<EOF
#     cd $DOCKER_COMPOSE_DIRECTORY
#     echo "Current frontend version:"
#     grep "$DOCKER_HUB_USER/$FRONTEND_IMAGE" docker-compose.yml
#     echo "Current backend version:"
#     grep "$DOCKER_HUB_USER/$BACKEND_IMAGE" docker-compose.yml
# EOF
ssh -i "$PRIVATE_KEY" "$HOST" "cd $DOCKER_COMPOSE_DIRECTORY && \
    echo -n 'Current frontend version: ' && \
    grep '$DOCKER_HUB_USER/$FRONTEND_IMAGE' docker-compose.yml | awk -F ':' '{print \$3}' && \
    echo -n 'Current backend version: ' && \
    grep '$DOCKER_HUB_USER/$BACKEND_IMAGE' docker-compose.yml | awk -F ':' '{print \$3}'"

echo -n "Please provide the version tag for the new deployment: "
read version_tag
    if [ -z "$version_tag" ]; then
        echo "Version tag cannot be empty! Please provide a valid version tag."
        exit 1
    fi

# Build the frontend image
echo "Building the frontend image..."
cd ./frontend || exit 1
"$BUILD_SCRIPT" <<EOF > /dev/null 2>&1
$FRONTEND_IMAGE
$version_tag
EOF

# Go back to the root directory
cd ..

# Build the backend image
echo "Building the backend image..."
cd ./backend || exit 1
"$BUILD_SCRIPT" <<EOF > /dev/null 2>&1
$BACKEND_IMAGE
$version_tag
EOF


# Access the remote host, to update the docker-compose file
echo "Updating the docker-compose file on the remote host..."
ssh -i "$PRIVATE_KEY" "$HOST" "\
cd $DOCKER_COMPOSE_DIRECTORY && \
sed -i 's|${DOCKER_HUB_USER}/${FRONTEND_IMAGE}:.*|${DOCKER_HUB_USER}/${FRONTEND_IMAGE}:$version_tag|g' docker-compose.yml && \
sed -i 's|${DOCKER_HUB_USER}/${BACKEND_IMAGE}:.*|${DOCKER_HUB_USER}/${BACKEND_IMAGE}:$version_tag|g' docker-compose.yml && \
docker compose up -d > /dev/null 2>&1 && \
echo 'Deployment completed successfully!'"

# Go back to the root directory
cd ..

# Deploy the frontend application
cd ./frontend || exit 1

# Deploy on firebase
echo "Deploying the frontend application on Firebase..."
firebase deploy --only hosting > /dev/null 2>&1

# Android deployment
echo "Building the Android application..."
"$ANDROID_BUILD_SCRIPT" > /dev/null 2>&1 <<EOF
$version_tag
EOF

echo "Android application built successfully!"

