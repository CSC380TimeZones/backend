# JetLagJelly Backend

To get everything set up for your development environment, follow these steps
for:

- [IntelliJ](#intellij-idea)
- [Any Other IDE](#any-other-ide)

# IntelliJ Idea

0. Clone the repo, enter the directory, and add the following files:

`.env`:

```env
MONGODB_USER=""
MONGODB_PASSWORD=""
MONGODB_DATABASE=""
MONGODB_LOCAL_PORT=
MONGODB_DOCKER_PORT=
MONGODB_HOSTNAME=""
REDIRECT_URL=""
```

`src/main/resources/credentials.json`:

A credentials file, from google cloud console.

```bash
git clone https://github.com/CSC380TimeZones/backend.git # Clone using HTTP
git clone git@github.com:CSC380TimeZones/backend.it # Clone using SSH
# If you don't know the difference between SSH and HTTP, use HTTP.
cd backend
```

1. Open the folder in IntelliJ

2. Click 'Edit Configurations'
   ![image](https://user-images.githubusercontent.com/46410314/226192542-1dbfbf2f-09ab-45f7-be13-39117b6f6de6.png)

3. Add a new maven configuration that looks like the one below
   ![image](https://user-images.githubusercontent.com/46410314/226192561-64c6771e-5b6a-4b09-81a1-396465a4f9d9.png)

4. To run the dev server, simply click the run button in the top right, and your
   server will be up and running on `localhost:8080`

# Any other IDE

Follow the same installation step 0, but instead, run the following
command whenever you want to run

```bash
./mvnw spring-boot:run
```

# Testing and Deployment

Since our program integrates with MongoDB, you are required to have an instance of mongodb running to complete all of the tests.

If you have [docker](https://docker.io) installed, it is very easy to set up:

### 1. Run `docker compose up -d`

Without a java class built, the `backend-app` container is going to continuously restart. That's ok, we don't need it for now!

### 2. User `docker ps` to get the id of the `backend-app` container, and stop it

This will leave us with a working mongo instance with the same
configurations that our production version will have!

### 3. Use [Mongo Compass](https://www.mongodb.com/products/compass) to initialize a new Database with the same name as you specified in `.env`

This will allow the program to connect to the database and get working.

### 4. Run tests!

If you're using an IDE that supports junit tests, a play button will appear next to the function names in the `test` folder, and you can run them individually.

If you are not using an IDE that supports that, All tests will automatically be run when packaging the application, so run `./mvnw clean package`

**If all the tests pass, run `./mvnw clean package`, and you should have a target folder with the working application!**

Use `docker compose down` to stop all the containers, then use `docker compose up --build -d` to start your application again.

**Your deployment should now be live!**
