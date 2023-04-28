FROM amazoncorretto:17


# Uncomment these to build the jar when building the image

# WORKDIR /tmp
# COPY . /tmp
# RUN /tmp/mvnw -Dmaven.test.skip=true clean package
# WORKDIR /app
# RUN cp /tmp/target/backend-0.0.1-SNAPSHOT.jar /app
# RUN rm -rf /tmp

# Otherwise, use these lines

WORKDIR /app
COPY . /app

ENTRYPOINT ["java","-jar","/app/target/backend-0.0.1-SNAPSHOT.jar"]
