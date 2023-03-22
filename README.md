# JetLagJelly Backend

To get everything set up for your development environment, follow these steps
for:

- [IntelliJ](#intellij-idea)
- [Any Other IDE](#any-other-ide)

# IntelliJ Idea

0. Clone the repo, enter the directory

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

0. Clone the repo, enter the directory

```bash
git clone https://github.com/CSC380TimeZones/backend.git # Clone using HTTP
git clone git@github.com:CSC380TimeZones/backend.it # Clone using SSH
# If you don't know the difference between SSH and HTTP, use HTTP.
cd backend
```

1. Open the folder in the IDE

2. To run the server, open a terminal in the project folder and run:

```bash
./mvnw spring-boot:run
```
