# CodeJudge - Online DSA Execution Platform

This project is a high-complexity system that moves beyond basic CRUD by implementing a decoupled, message-driven architecture to handle code compilation and execution in a secure, sandboxed environment (Docker containers).

## Setup

### Step 1 - Build the images where the actual code gets executed
```
cd src/main/resources/judge-image
docker build -t cpp-runner .
cd ../judge-image-py
docker build -t py-runner .
```

### Step 2 - Create the directories that can get accessed by both the runners and the app
```
sudo mkdir -p /judge-workdir
sudo chmod 777 /judge-workdit
```

### Step 3 - Build the image of the app
```
cd ../../../../
docker compose up --build -d
```

The app should be available at the address you've set up in SECURITY_WEBSITE_DOMAIN variable.

## .env configuration
```
ENDAVA_URL=URL
ENDAVA_DB=endava
ENDAVA_USER=root
ENDAVA_PASS=password
MYSQL_ROOT_PASSWORD=password
JWT_SECRET_KEY=generate_one
SECURITY_ADMIN_EMAIL=admin@example.com
SECURITY_WEBSITE_DOMAIN=https://example.com
MAIL_CONFIRMATION=no-reply@example.com
MAILJET_APIKEY_PUBLIC=redacted
MAILJET_APIKEY_PRIVATE=redacted
SECURITY_COOKIE_SECURE=true
SECURITY_COOKIE_SAME_SITE=Lax
SECURITY_LOGIN_MAX_ATTEMPTS=3
SECURITY_LOGIN_FAILURE_WINDOW=PT15M
SECURITY_LOGIN_LOCK_DURATION=PT15M
```
* Note: Currently the frontend URL to the backend is hard-coded in client/src/environments/environments.ts
  * Should be fixed in a later commit